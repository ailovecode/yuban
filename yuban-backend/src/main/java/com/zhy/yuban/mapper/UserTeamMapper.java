package com.zhy.yuban.mapper;

import com.zhy.yuban.model.domain.User;
import com.zhy.yuban.model.domain.UserTeam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author DELL
* @description 针对表【user_team(用户-队伍表)】的数据库操作Mapper
* @createDate 2025-06-26 22:00:29
* @Entity com.zhy.yuban.model.domain.UserTeam
*/
public interface UserTeamMapper extends BaseMapper<UserTeam> {

    List<User> selectUserByTeamId(long teamId);

    Long selectFirstById(Long teamId);
}




