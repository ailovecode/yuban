package com.zhy.yuban.service.impl;

import com.zhy.yuban.service.TeamService;
import com.zhy.yuban.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @version 1.0
 * @Author zhy
 * @Date 2025/6/27 12:00
 */
@SpringBootTest
class TeamServiceImplTest {

    @Resource
    private TeamService teamService;

    @Resource
    private UserService userService;

    @Test
    void teamList() {

    }
}