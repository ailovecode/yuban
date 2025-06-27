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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
                StringUtils.equals(oldTeam.getTeamName(), teamTitle)) {
            // 校验是否合法
            if(teamTitle.length() > 20) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍名称太长了！");
            }
            updateWrapper.set("teamName", teamUpdateRequest.getTeamName());
        }
        // 5. 校验队伍描述是否需要修改
        String description = teamUpdateRequest.getDescription();
        if(StringUtils.isNotBlank(description) &&
                StringUtils.equals(oldTeam.getDescription(), description)) {
            // 校验是否合法
            if(description.length() > 512) {
                throw  new BusinessException(ErrorCode.PARAMS_ERROR, "队伍描述不合法");
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
        long isExist = userTeamService.count(userTeamQueryWrapper);
        if(isExist > 0) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "用户已在队伍中！");
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
}




