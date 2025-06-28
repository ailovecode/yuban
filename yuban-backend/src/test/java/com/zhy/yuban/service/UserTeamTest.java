package com.zhy.yuban.service;

import com.zhy.yuban.mapper.UserTeamMapper;
import com.zhy.yuban.model.domain.User;
import com.zhy.yuban.model.vo.UserVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0
 * @Author zhy
 * @Date 2025/6/27 11:39
 */
@SpringBootTest
class UserTeamTest {

    @Resource
    private UserTeamMapper userTeamMapper;

    @Test
    void test() {
        List<User> teamUsersSource =  userTeamMapper.selectUserByTeamId(3l);
        List<UserVo> teamUsersVoTarget = new ArrayList<>();
        for(User userSource : teamUsersSource) {
            UserVo userTarget = new UserVo();
            BeanUtils.copyProperties(userSource, userTarget);
            teamUsersVoTarget.add(userTarget);
        }
        System.out.println(teamUsersVoTarget);
    }
}
