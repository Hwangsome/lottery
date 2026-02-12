package com.clinic.lottery.auth.dto;

import lombok.Data;

/**
 * 登录响应
 */
@Data
public class LoginResponse {

    /**
     * JWT Token
     */
    private String token;

    /**
     * 过期时间（秒）
     */
    private Long expiresIn;

    /**
     * 用户信息
     */
    private UserInfoVO userInfo;

    /**
     * 是否新用户
     */
    private Boolean isNewUser;
}
