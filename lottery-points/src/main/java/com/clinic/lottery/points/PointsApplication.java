package com.clinic.lottery.points;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import com.clinic.lottery.points.config.PointsCheckinProperties;

/**
 * 积分服务启动类
 */
@SpringBootApplication(scanBasePackages = "com.clinic.lottery")
@EnableDiscoveryClient
@EnableDubbo
@MapperScan("com.clinic.lottery.points.mapper")
@EnableConfigurationProperties(PointsCheckinProperties.class)
public class PointsApplication {

    public static void main(String[] args) {
        SpringApplication.run(PointsApplication.class, args);
    }
}
