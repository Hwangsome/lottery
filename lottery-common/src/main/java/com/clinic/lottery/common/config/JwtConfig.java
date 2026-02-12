package com.clinic.lottery.common.config;

import com.clinic.lottery.common.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * JWT 配置
 */
@Configuration
public class JwtConfig {

    @Value("${jwt.secret:lottery-secret-key-must-be-at-least-32-characters}")
    private String secret;

    @Value("${jwt.expire-seconds:604800}")
    private long expireSeconds;

    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil(secret, expireSeconds);
    }
}
