package com.zhy.yuban.service;

import com.zhy.yuban.model.domain.User;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;

/**
 * @version 1.0
 * @Author zhy
 * @Date 2025/5/21 17:13
 */
@SpringBootTest
public class UseRediTest {

    @Resource
    private RedisTemplate redisTemplate;

    @Test
    void test(){
        ValueOperations valueOperations = redisTemplate.opsForValue();

        // 增
        valueOperations.set("name","zhy");
        valueOperations.set("age",18);
        valueOperations.set("sex","男");
        User user = new User();
        user.setId(1L);
        user.setUsername("zhy");
        valueOperations.set("user",user);

        // 查
        valueOperations.get("name");
        valueOperations.get("age");
        valueOperations.get("sex");
        valueOperations.get("user");


    }
}
