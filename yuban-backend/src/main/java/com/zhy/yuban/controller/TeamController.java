package com.zhy.yuban.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhy.yuban.common.BaseResponse;
import com.zhy.yuban.common.ErrorCode;
import com.zhy.yuban.common.ResultUtil;
import com.zhy.yuban.exception.BusinessException;
import com.zhy.yuban.model.domain.Team;
import com.zhy.yuban.model.domain.User;
import com.zhy.yuban.model.dto.TeamQuery;
import com.zhy.yuban.model.request.TeamAddRequest;
import com.zhy.yuban.model.request.TeamJoinRequest;
import com.zhy.yuban.model.request.TeamQuitRequest;
import com.zhy.yuban.model.request.TeamUpdateRequest;
import com.zhy.yuban.model.vo.TeamUserVo;
import com.zhy.yuban.service.TeamService;
import com.zhy.yuban.service.UserService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @version 1.0
 * @Author zhy
 * @Date 2025/6/26 17:51
 */
@Api(tags = "队伍模块")
@RestController
@RequestMapping("/team")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@Slf4j
public class TeamController {

    @Resource
    private TeamService teamService;

    @Resource
    private UserService userService;


    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request) {

        // 1. 校验参数书是否为空？
        if(teamAddRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "请求队伍信息为空！");
        }
        // 2. 校验用户是否登录
        User loginUser = userService.getLoginUser(request);
        if(loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }

        Long teamId = teamService.addTeam(teamAddRequest, loginUser);
        if(teamId == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "队伍添加失败！");
        }
        return ResultUtil.success(teamId);
    }

    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest, HttpServletRequest request) {
        // 1. 校验数据是否为空？
        if(teamJoinRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        // 2. 获取登录信息
        User loginUser = userService.getLoginUser(request);
        Boolean result = teamService.joinTeam(teamJoinRequest, loginUser);
        if(Boolean.FALSE.equals(result)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "加入队伍失败！");
        }
        return ResultUtil.success(true);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody TeamQuitRequest deleteTeamRequest, HttpServletRequest request) {
        // 1. 校验队伍信息
        if(deleteTeamRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "请求队伍 id 为空！");
        }
        // 2. 获取登录用户信息
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.deleteTeam(deleteTeamRequest, loginUser);
        if(!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "队伍解散失败！");
        }
        return ResultUtil.success(true);
    }

    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(@RequestBody TeamQuitRequest teamQuitRequest, HttpServletRequest request) {
        // 1. 校验请求参数信息是否为空
        if(teamQuitRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        // 2. 获取登录信息
        User loginUser = userService.getLoginUser(request);
        Boolean result = teamService.quitTeam(teamQuitRequest, loginUser);
        if(Boolean.FALSE.equals(result)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "退出队伍失败！");
        }
        return ResultUtil.success(true);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest, HttpServletRequest request) {
        //  1. 判断请求参数是否为空？
        if(teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "请求队伍信息为空！");
        }
        // 2. 获取登录用户信息
        User loginUser = userService.getLoginUser(request);
        // 3. 执行队伍信息更新服务
        boolean result = teamService.updateTeam(teamUpdateRequest, loginUser);
        if(!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "队伍信息更新失败！");
        }
        return ResultUtil.success(true);
    }

    @GetMapping("/get")
    public BaseResponse<Team> getTeam(@RequestParam("teamId") long teamId) {
        if(teamId < 0) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "请求队伍 id 为空！");
        }
        Team team = teamService.getById(teamId);
        if(team == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取队伍信息失败！");
        }
        return ResultUtil.success(team);
    }

    @GetMapping("/get/myTeam")
    public BaseResponse<List<TeamUserVo>> getTeamByUserId(HttpServletRequest request) {
        // 1. 获取登录信息
        User loginUser = userService.getLoginUser(request);
        // 2. 调用业务实现
        List<TeamUserVo> result = teamService.getTeamByUserId(loginUser);
        if(result == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "用户或许还未加入队伍");
        }
        return ResultUtil.success(result);
    }

    @GetMapping("/get/manageTeam")
    public BaseResponse<List<TeamUserVo>> getManageTeamByUserId(HttpServletRequest request) {
        // 1. 获取登录用户信息
        User loginUser = userService.getLoginUser(request);
        // 2. 构造 TeamQuery 实体类
        TeamQuery teamQuery = new TeamQuery();
        teamQuery.setCaptainId(loginUser.getId());
        // 3. 调用 teamList 服务
        List<TeamUserVo> teamList = teamService.teamList(teamQuery, loginUser);
        if(teamList == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "该用户没有管理的队伍！");
        }
        return ResultUtil.success(teamList);
    }

    @GetMapping("/list")
    public BaseResponse<List<TeamUserVo>> teamList(TeamQuery teamQuery, HttpServletRequest request) {
        if(teamQuery == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "请求队伍信息为空！");
        }
        User loginUser = userService.getLoginUser(request);
        List<TeamUserVo> listResult = teamService.teamList(teamQuery, loginUser);
        if(listResult == null || listResult.isEmpty()) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "查询信息为空！");
        }
        return ResultUtil.success(listResult);
    }

    @GetMapping("/search")
    public BaseResponse<List<TeamUserVo>> searchTeam(String searchTeam, HttpServletRequest request) {
        if(searchTeam == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "请求参数为空！");
        }
        List<TeamUserVo> listResult = teamService.searchTeam(searchTeam);
        if(listResult == null || listResult.isEmpty()) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "搜索队伍为空！");
        }
        return ResultUtil.success(listResult);
    }

    @GetMapping("/list/page")
    public BaseResponse<Page<Team>> pageTeam(TeamQuery teamQuery) {
        if(teamQuery == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "请求队伍信息为空！");
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamQuery, team);
        Page<Team> page = new Page<>(teamQuery.getPageNum(), teamQuery.getPageSize());
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
        Page<Team> pageResult = teamService.page(page, queryWrapper);
        return ResultUtil.success(pageResult);
    }
}
