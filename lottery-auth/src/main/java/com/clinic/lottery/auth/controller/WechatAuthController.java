package com.clinic.lottery.auth.controller;

import com.clinic.lottery.auth.dto.LoginResponse;
import com.clinic.lottery.auth.dto.WechatLoginRequest;
import com.clinic.lottery.auth.service.WechatAuthService;
import com.clinic.lottery.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 微信认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "用户认证", description = "微信小程序登录相关接口")
public class WechatAuthController {

    private final WechatAuthService wechatAuthService;

    public WechatAuthController(WechatAuthService wechatAuthService) {
        this.wechatAuthService = wechatAuthService;
    }

    @PostMapping("/login")
    @Operation(summary = "微信登录", description = "使用微信 code 登录，返回 token 和用户信息")
    public Result<LoginResponse> login(@Valid @RequestBody WechatLoginRequest request) {
        log.info("微信登录请求");
        LoginResponse response = wechatAuthService.login(request);
        return Result.success(response);
    }
}
