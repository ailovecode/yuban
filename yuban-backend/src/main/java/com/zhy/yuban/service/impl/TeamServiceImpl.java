package com.zhy.yuban.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhy.yuban.common.ErrorCode;
import com.zhy.yuban.exception.BusinessException;
import com.zhy.yuban.mapper.TeamMapper;
import com.zhy.yuban.mapper.UserTeamMapper;
import com.zhy.yuban.model.domain.Team;
import com.zhy.yuban.model.domain.User;
import com.zhy.yuban.model.domain.UserTeam;
import com.zhy.yuban.model.dto.TeamQuery;
import com.zhy.yuban.model.request.TeamAddRequest;
import com.zhy.yuban.model.request.TeamJoinRequest;
import com.zhy.yuban.model.request.TeamQuitRequest;
import com.zhy.yuban.model.request.TeamUpdateRequest;
import com.zhy.yuban.model.vo.TeamUserVo;
import com.zhy.yuban.model.vo.UserVo;
import com.zhy.yuban.service.TeamService;
import com.zhy.yuban.service.UserTeamService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
* @author DELL
* @description 针对表【team(队伍表)】的数据库操作Service实现
* @createDate 2025-06-26 17:15:27
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{

    @Resource
    private UserTeamService userTeamService;

    @Resource
    private UserTeamMapper userTeamMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addTeam(TeamAddRequest teamAddRequest, User loginUser) {
        // 校验队伍信息是否合法
        // 1. 队伍标题  <= 20
        String teamTitle = teamAddRequest.getTeamName();
        if(StringUtils.isBlank(teamTitle) || teamTitle.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍名称太长了！");
        }

        // 2. 队伍人数  >= 1 && <= 20
        int teamNum = teamAddRequest.getMaxNumber();
        if(teamNum < 1 || teamNum > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数过多！");
        }

        // 3. 描述 <=  512
        String description = teamAddRequest.getDescription();
        if(StringUtils.isBlank(description) || description.length() > 512) {
            throw  new BusinessException(ErrorCode.PARAMS_ERROR, "队伍描述不合法");
        }

        // 4. 密码 >= 3 且 <= 10
        String password = teamAddRequest.getTeamPassword();
        if(StringUtils.isBlank(password) || password.length() < 3 || password.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍密码不合法！");
        }

        // 5. 校验用户创建的队伍数 <= 5  超过 5 ，需要收费
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("captainId", loginUser.getId());
        long count = this.count(queryWrapper);
        if(count >= 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户创建队伍的数量已上限，请充值会员获得额外创建权限！");
        }

        // 4. 插入队伍信息到队伍表中
        Team team = new Team();
        team.setCaptainId(loginUser.getId());
        team.setMaxNumber(teamNum);
        team.setTeamName(teamTitle);
        team.setDescription(description);
        team.setRemainNumber(teamNum - 1);
        team.setTeamPassword(password);
        boolean save = this.save(team);
        Long teamId = team.getTeamId();
        if(!save || teamId == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "队伍创建失败！");
        }

        // 5. 插入用户 - 队伍关系表
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(loginUser.getId());
        userTeam.setTeamId(team.getTeamId());
        userTeam.setCreateTime(new Date());
        boolean save1 = userTeamService.save(userTeam);
        if(!save1 || userTeam.getId() == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "队伍创建失败！");
        }
        return teamId;
    }

    @Override
    public List<TeamUserVo> teamList(TeamQuery teamQuery, User loginUser) {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        // 1. 判断队伍 id
        if(teamQuery.getTeamId() != null) {
            queryWrapper.eq("teamId", teamQuery.getTeamId());
        }
        // 2. 判断队伍名称
        if(StringUtils.isNotBlank(teamQuery.getTeamName())) {
            queryWrapper.like("teamName", teamQuery.getTeamName());
        }
        // 3. 判断队长 id
        if(teamQuery.getCaptainId() != null) {
            queryWrapper.eq("captainId", teamQuery.getCaptainId());
        }
        // 4. 查找队伍描述
        if(StringUtils.isNotBlank(teamQuery.getDescription())) {
            queryWrapper.like("description", teamQuery.getDescription());
        }
        // 5. 查找队伍最大人数
        if(teamQuery.getMaxNumber() != null) {
            queryWrapper.eq("maxNumber", teamQuery.getMaxNumber());
        }
        // 6. 判断是否是用户
        if(loginUser.getUserRole() == 0) {
            queryWrapper.gt("remainNumber", 0);
        }
        // 7. 查询队伍列表信息
        List<Team> teamList = this.list(queryWrapper);
        if(teamList == null || teamList.isEmpty()) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "未查找到队伍信息!");
        }
        List<TeamUserVo> result = new ArrayList<>();
        // 8. 关联表查找用户信息
        for(Team team : teamList) {
            // 返回队伍信息
            TeamUserVo teamUserVo = new TeamUserVo();
            BeanUtils.copyProperties(team, teamUserVo);
            long teamId = team.getTeamId();

            // 原队员列表
            List<User> teamUsersSource =  userTeamMapper.selectUserByTeamId(teamId);
            // 返回队员列表
            List<UserVo> teamUsersVoTarget = new ArrayList<>();
            for(User userSource : teamUsersSource) {
                UserVo userTarget = new UserVo();
                BeanUtils.copyProperties(userSource, userTarget);
                teamUsersVoTarget.add(userTarget);
            }
            teamUserVo.setUsers(teamUsersVoTarget);
            result.add(teamUserVo);
        }
        return result;
    }

    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser) {
        // 1. 查询队伍是否存在
        Team oldTeam = this.getById(teamUpdateRequest.getTeamId());
        if(oldTeam == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "没有这个队伍信息!");
        }
        // 2. 校验请求得到权限，只有队长可以修改队伍信息
        if(!Objects.equals(loginUser.getId(), teamUpdateRequest.getCaptainId())) {
            throw new BusinessException(ErrorCode.NO_AUTH, "只有队伍管理员可以更新！");
        }
        // 3. 对比新旧信息来更新
        UpdateWrapper<Team> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("teamId", teamUpdateRequest.getTeamId());
        // 4. 校验队伍名称是否需要修改
        String teamTitle = teamUpdateRequest.getTeamName();
        if(StringUtils.isNotBlank(teamTitle) &&
                !StringUtils.equals(oldTeam.getTeamName(), teamTitle)) {
            // 校验是否合法
            if(teamTitle.length() > 20) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍名称太长了！");
            }
            updateWrapper.set("teamName", teamUpdateRequest.getTeamName());
        }
        // 5. 校验队伍描述是否需要修改
        String description = teamUpdateRequest.getDescription();
        if (description != null) {
            if (description.length() > 512) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍描述不合法");
            }
            updateWrapper.set("description", description);
        }
        // 6. 校验队伍最大人数是否需要修改
        Integer maxNumber = teamUpdateRequest.getMaxNumber();
        if(maxNumber != null && !Objects.equals(maxNumber, oldTeam.getMaxNumber())) {
            int originalNumber = oldTeam.getMaxNumber() - oldTeam.getRemainNumber();
            // 校验是否合法
            if((maxNumber < originalNumber) || maxNumber > 20) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数不合法！");
            }
            // 更新最大坑位
            updateWrapper.set("maxNumber", maxNumber);
            // 更改空闲坑位
            updateWrapper.set("remainNumber", maxNumber - originalNumber);
        }
        // 7. 校验队伍密码是否需要修改
        String password = teamUpdateRequest.getTeamPassword();
        if(StringUtils.isNotBlank(password) && StringUtils.equals(password, oldTeam.getTeamPassword())) {
            // 检验是否合法
            if(password.length() < 3 || password.length() > 30) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍密码不合法!");
            }
            updateWrapper.set("teamPassword", password);
        }
        return this.update(updateWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser) {
        // 1. 判断队伍是否存在？
        Team team = this.getById(teamJoinRequest.getTeamId());
        if(team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "加入的队伍不存在！");
        }
        // 2. 校验是否已经加入队伍
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("userId", loginUser.getId());
        userTeamQueryWrapper.eq("teamId", teamJoinRequest.getTeamId());
        long isExist = userTeamService.count(userTeamQueryWrapper);
        if(isExist > 0) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "你已在队伍中！");
        }
        // 3. 校验密码是否正确？
        String inputPassword = teamJoinRequest.getPassword();
        if(StringUtils.isBlank(inputPassword) ||
                !StringUtils.equals(inputPassword, team.getTeamPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "输入的密码不正确！");
        }
        // 4. 判断队伍是否还有坑位？
        int currentNum = team.getRemainNumber();
        if(currentNum < 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人员已满！");
        }
        // 5. 判断用户所在的队伍的数量是否上限？
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", loginUser.getId());
        long count = userTeamService.count(queryWrapper);
        if(count >= 5) {
            // 超限，不能再加入或者创建队伍
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "你加入队伍的数量已达上限！");
        }
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(loginUser.getId());
        userTeam.setTeamId(team.getTeamId());
        userTeam.setJoinTime(new Date());
        // 6. 加入队伍并更新队伍表中的剩余坑位  开始事务
        boolean save = userTeamService.save(userTeam);
        if(!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "加入队伍失败！");
        }
        UpdateWrapper<Team> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("teamId", team.getTeamId());
        updateWrapper.set("remainNumber", currentNum - 1);
        boolean update = this.update(updateWrapper);
        if(!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "加入队伍失败！");
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser) {
        // 1. 校验队伍是否存在
        Team team = this.getById(teamQuitRequest.getTeamId());
        if(team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "该队伍不存在！");
        }

        // 2. 校验登录的用户是否已加入队伍
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("userId", loginUser.getId());
        userTeamQueryWrapper.eq("teamId", team.getTeamId());
        long isExist = userTeamService.count(userTeamQueryWrapper);
        if(isExist < 1) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "用户不存在这个队伍中！");
        }

        // 退出队伍
        int currentNum = team.getMaxNumber() - team.getRemainNumber();
        if(currentNum == 1) {
            // 只有该用户自己，直接将队伍解散，在当前分支结束后直接结束
            // 1. 删除队伍所关联的用户
            QueryWrapper<UserTeam> userTeamQueryWrapper1 = new QueryWrapper<>();
            userTeamQueryWrapper1.eq("teamId", team.getTeamId());
            boolean removeUserTeam = userTeamService.remove(userTeamQueryWrapper1);
            if(!removeUserTeam) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "退出队伍失败！");
            }
            // 2. 删除队伍
            QueryWrapper<Team> teamQueryWrapper = new QueryWrapper<>();
            teamQueryWrapper.eq("teamId", team.getTeamId());
            boolean removeTeam = this.remove(teamQueryWrapper);
            if(!removeTeam) {
                throw  new BusinessException(ErrorCode.SYSTEM_ERROR, "退出队伍失败！");
            }
        } else if(currentNum >= 2){
            // 1. 将自己从队伍中退出
            QueryWrapper<UserTeam> userTeamQueryWrapper1 = new QueryWrapper<>();
            userTeamQueryWrapper1.eq("userId", loginUser.getId());
            userTeamQueryWrapper1.eq("teamId", team.getTeamId());
            boolean removeUserTeam = userTeamService.remove(userTeamQueryWrapper1);
            if(!removeUserTeam) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "退出队伍失败！");
            }
            // 定义查询 wrapper
            UpdateWrapper<Team> teamUpdateWrapper = new UpdateWrapper<>();
            teamUpdateWrapper.eq("teamId", team.getTeamId());
            // 2. 判断是否是队长
            if (Objects.equals(team.getCaptainId(), loginUser.getId())) {
                // 查找队员列表中入队时间最早的（只需查找关联表即可）
                QueryWrapper<UserTeam> userTeamQueryWrapper2 = new QueryWrapper<>();
                userTeamQueryWrapper2.eq("teamId", team.getTeamId());
                Long newCaptainId = userTeamMapper.selectFirstById(team.getTeamId());
                if (newCaptainId == null) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查找用户错误！");
                }
                // 更新队长信息
                teamUpdateWrapper.set("captainId", newCaptainId);
            }
            // 更新剩余坑位数量
            teamUpdateWrapper.set("remainNumber", team.getRemainNumber() + 1);
            boolean update = this.update(teamUpdateWrapper);
            if (!update) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新队长信息错误！");
            }
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTeam(TeamQuitRequest deleteTeamRequest, User loginUser) {
        Long teamId = deleteTeamRequest.getTeamId();
        // 1. 获取队伍信息
        Team team = this.getById(teamId);
        if(team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "该队伍不存在！");
        }
        // 2. 校验权限是否为队长
        if(!Objects.equals(loginUser.getId(), team.getCaptainId())) {
            throw new BusinessException(ErrorCode.NO_AUTH, "用户没有权限解散队伍！");
        }
        // 3. 删除队伍关联队员信息
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("teamId", teamId);
        boolean delete = userTeamService.remove(userTeamQueryWrapper);
        if (!delete) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "解散队伍失败！");
        }
        // 4. 删除队伍
        boolean removeTeam = this.removeById(teamId);
        if(!removeTeam) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "解散队伍失败！");
        }
        return true;
    }

    @Override
    public List<TeamUserVo> getTeamByUserId(User loginUser) {
        // 1. 通过关联表获取用户加入队伍id列表
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("userId", loginUser.getId());
        List<UserTeam> listUserTeam = userTeamService.list(userTeamQueryWrapper);
        if(listUserTeam.isEmpty()) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "未找到用户加入的队伍！");
        }
        // 2. 通过遍历列表信息调用 teamList 返回包装类 UserTeamVo
        List<TeamUserVo> result = new ArrayList<>();
        for(UserTeam userTeam : listUserTeam) {
            // 有了 teamId 查找队伍相关的信息以及相关的人员
            long teamId = userTeam.getTeamId();

            // 获取队伍信息
            Team team = this.getById(teamId);
            if(team == null) {
                throw new BusinessException(ErrorCode.NULL_ERROR, "未查找到队伍信息!");
            }

            // 包装队伍信息，包含队员信息
            TeamUserVo teamUserVo = new TeamUserVo();
            BeanUtils.copyProperties(team, teamUserVo);
            // 原队员列表
            List<User> teamUsers =  userTeamMapper.selectUserByTeamId(teamId);
            // 包装用户信息
            List<UserVo> teamUsersVoTarget = new ArrayList<>();
            for(User userSource : teamUsers) {
                UserVo userTarget = new UserVo();
                BeanUtils.copyProperties(userSource, userTarget);
                teamUsersVoTarget.add(userTarget);
            }
            teamUserVo.setUsers(teamUsersVoTarget);
            result.add(teamUserVo);
        }
        return result;
    }

    @Override
    public List<TeamUserVo> searchTeam(String searchTeam) {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("teamName", searchTeam)
                .or()
                .like("description", searchTeam);
        // 7. 查询队伍列表信息
        List<Team> teamList = this.list(queryWrapper);
        if(teamList == null || teamList.isEmpty()) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "未查找到队伍信息!");
        }
        List<TeamUserVo> result = new ArrayList<>();
        // 8. 关联表查找用户信息
        for(Team team : teamList) {
            // 返回队伍信息
            TeamUserVo teamUserVo = new TeamUserVo();
            BeanUtils.copyProperties(team, teamUserVo);
            long teamId = team.getTeamId();

            // 原队员列表
            List<User> teamUsersSource =  userTeamMapper.selectUserByTeamId(teamId);
            // 返回队员列表
            List<UserVo> teamUsersVoTarget = new ArrayList<>();
            for(User userSource : teamUsersSource) {
                UserVo userTarget = new UserVo();
                BeanUtils.copyProperties(userSource, userTarget);
                teamUsersVoTarget.add(userTarget);
            }
            teamUserVo.setUsers(teamUsersVoTarget);
            result.add(teamUserVo);
        }
        return result;
    }
}




