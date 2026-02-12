package com.clinic.lottery.auth.dto;

import lombok.Data;

/**
 * 管理员登录响应
 */
@Data
public class AdminLoginResponse {

    /**
     * JWT Token
     */
    private String token;

    /**
     * 过期时间（秒）
     */
    private Long expiresIn;

    /**
     * 管理员信息
     */
    private AdminInfoVO adminInfo;
}
