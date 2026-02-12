package com.clinic.lottery.admin;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 管理后台服务启动类
 */
@SpringBootApplication(scanBasePackages = "com.clinic.lottery")
@EnableDiscoveryClient
@EnableDubbo
@MapperScan("com.clinic.lottery.admin.mapper")
public class AdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }
}
