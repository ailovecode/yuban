package com.zhy.yuban.service;

import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @version 1.0
 * @Author zhy
 * @Date 2025/6/26 10:45
 */
@SpringBootTest
public class RedissonTest {

    @Resource
    private RedissonClient redissonClient;

    @Test
    void test(){
        List<String> list = new ArrayList<>();
        list.add("xiaoyu");
        System.out.println("list: " + list.get(0));

        list.remove(0);

        RList<String> rList = redissonClient.getList("test-list");
        rList.add("xiaoyu");
        System.out.println("rlist: " + rList.get(0));
        rList.remove(0);

//        Map<String, Integer> map = redissonClient.getMap("test-map");

    }

}
