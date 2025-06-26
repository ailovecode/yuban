package com.zhy.yuban.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhy.yuban.model.domain.Team;
import com.zhy.yuban.service.TeamService;
import com.zhy.yuban.mapper.TeamMapper;
import org.springframework.stereotype.Service;

/**
* @author DELL
* @description 针对表【team(队伍表)】的数据库操作Service实现
* @createDate 2025-06-26 17:15:27
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{

}




