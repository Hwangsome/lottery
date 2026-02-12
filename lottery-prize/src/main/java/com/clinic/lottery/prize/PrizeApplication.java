package com.clinic.lottery.prize;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 抽奖服务启动类
 */
@SpringBootApplication(scanBasePackages = "com.clinic.lottery")
@EnableDiscoveryClient
@EnableDubbo
@MapperScan("com.clinic.lottery.prize.mapper")
public class PrizeApplication {

    public static void main(String[] args) {
        SpringApplication.run(PrizeApplication.class, args);
    }
}
