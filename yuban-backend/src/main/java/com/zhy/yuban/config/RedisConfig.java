package com.zhy.yuban.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @version 1.0
 * @Author zhy
 * @Date 2025/6/26 10:40
 */
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedisConfig {
    private String host;

    private Integer port;

    @Bean
    public RedissonClient redissonClient() {
        // 1. 创建配置
        Config config = new Config();
        String redisAddress = String.format("redis://%s:%s", host, port);
        //  使用单个Redis，没有开集群 useClusterServers()  设置地址和使用库
        config.useSingleServer().setAddress(redisAddress).setDatabase(3);
        // 2. 创建实例
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }
}
