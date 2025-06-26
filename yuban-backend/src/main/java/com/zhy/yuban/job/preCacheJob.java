package com.zhy.yuban.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhy.yuban.model.domain.User;
import com.zhy.yuban.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 预热缓存定时任务
 *
 * @version 1.0
 * @Author zhy
 * @Date 2025/6/6 16:31
 */
@Component
@Slf4j
public class preCacheJob {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private UserService userService;

    @Resource
    private RedissonClient redissonClient;

    // 重要用户list
    List<Long> userList = new ArrayList<>(Arrays.asList(1L,2L,3L,4L,5L,6L,7L,8L,9L,10L));

    // 针对用户做首页缓存预热 每天 23:59 运行
    @Scheduled(cron = "0 47 11 * * ?")
    private void doSearchCommendUser() {
        RLock lock = redissonClient.getLock("yuban:precachejob:docache:lock");

        try {
            if(lock.tryLock(0, -1 , TimeUnit.SECONDS)) {
                System.out.println("getLock: " + Thread.currentThread().getId());
                // 遍历重要用户
                for(Long userId : userList) {
                    // 为每个用户设计唯一的key
                    String redisKey = String.format("yuban:user:commend:%s", userId);
                    ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();

                    // 没有缓存从数据库中查询数据
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    Page<User> userPage = userService.page(new Page<>(1, 20), queryWrapper);

                    // 将查询的数据写入缓存中
                    try {
                        // 缓存有效时间为1小时
                        valueOperations.set(redisKey, userPage, 30000, TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        log.info("redis set key error: ", e);
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("doSearchCommendUser error {}", e);
        } finally {
            // 只能自己释放
            if(lock.isHeldByCurrentThread()) {
                System.out.println("unlock: "+ Thread.currentThread().getId());
                lock.unlock();
            }
        }

    }
}
