package com.clinic.lottery.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 微信登录请求
 */
@Data
public class WechatLoginRequest {

    /**
     * 微信登录 code
     */
    @NotBlank(message = "code不能为空")
    private String code;

    /**
     * 加密数据（用于获取用户信息）
     */
    private String encryptedData;

    /**
     * 加密向量
     */
    private String iv;
}
