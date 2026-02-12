package com.clinic.lottery.auth.controller;

import com.clinic.lottery.auth.dto.AdminLoginRequest;
import com.clinic.lottery.auth.dto.AdminLoginResponse;
import com.clinic.lottery.auth.service.AdminAuthService;
import com.clinic.lottery.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理员认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "管理员认证", description = "管理后台登录相关接口")
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    public AdminAuthController(AdminAuthService adminAuthService) {
        this.adminAuthService = adminAuthService;
    }

    @PostMapping("/login")
    @Operation(summary = "管理员登录", description = "使用用户名密码登录管理后台")
    public Result<AdminLoginResponse> login(@Valid @RequestBody AdminLoginRequest request,
                                            HttpServletRequest httpRequest) {
        log.info("管理员登录请求: {}", request.getUsername());
        String clientIp = getClientIp(httpRequest);
        AdminLoginResponse response = adminAuthService.login(request, clientIp);
        return Result.success(response);
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
