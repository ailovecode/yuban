package com.zhy.yuban.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhy.yuban.model.domain.Team;
import com.zhy.yuban.model.domain.User;
import com.zhy.yuban.model.dto.TeamQuery;
import com.zhy.yuban.model.request.TeamAddRequest;
import com.zhy.yuban.model.request.TeamJoinRequest;
import com.zhy.yuban.model.request.TeamQuitRequest;
import com.zhy.yuban.model.request.TeamUpdateRequest;
import com.zhy.yuban.model.vo.TeamUserVo;

import java.util.List;

/**
* @author DELL
* @description 针对表【team(队伍表)】的数据库操作Service
* @createDate 2025-06-26 17:15:27
*/
public interface TeamService extends IService<Team> {

    /**
     * 添加队伍信息
     *
     * @param teamAddRequest
     * @param loginUser
     */
    Long addTeam(TeamAddRequest teamAddRequest, User loginUser);

    /**
     * 搜索队伍信息
     *
     * @param teamQuery
     * @param loginUser
     * @return
     */
    List<TeamUserVo> teamList(TeamQuery teamQuery, User loginUser);

    /**
     * 更新队伍信息
     *
     * @param teamUpdateRequest
     * @param loginUser
     * @return
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser);

    /**
     * 用户加入队伍
     *
     * @param teamJoinRequest
     * @param loginUser
     * @return
     */
    Boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);

    /**
     * 用户退出队伍
     *
     * @param teamQuitRequest
     * @param loginUser
     * @return
     */
    Boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser);
}
