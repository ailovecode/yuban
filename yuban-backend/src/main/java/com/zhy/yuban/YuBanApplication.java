package com.zhy.yuban;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.zhy.yuban.mapper")
@EnableScheduling
public class YuBanApplication {

    public static void main(String[] args) {
        SpringApplication.run(YuBanApplication.class, args);
    }

}
