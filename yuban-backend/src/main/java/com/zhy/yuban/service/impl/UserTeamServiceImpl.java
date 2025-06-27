package com.zhy.yuban.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhy.yuban.model.domain.UserTeam;
import com.zhy.yuban.service.UserTeamService;
import com.zhy.yuban.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

/**
* @author DELL
* @description 针对表【user_team(用户-队伍表)】的数据库操作Service实现
* @createDate 2025-06-26 22:00:29
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




