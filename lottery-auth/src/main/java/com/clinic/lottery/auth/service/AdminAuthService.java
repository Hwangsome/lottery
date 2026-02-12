package com.clinic.lottery.auth.service;

import cn.hutool.crypto.digest.BCrypt;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.clinic.lottery.auth.dto.AdminInfoVO;
import com.clinic.lottery.auth.dto.AdminLoginRequest;
import com.clinic.lottery.auth.dto.AdminLoginResponse;
import com.clinic.lottery.auth.entity.Admin;
import com.clinic.lottery.auth.mapper.AdminMapper;
import com.clinic.lottery.common.constant.ErrorCode;
import com.clinic.lottery.common.exception.BizException;
import com.clinic.lottery.common.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理员认证服务
 */
@Slf4j
@Service
public class AdminAuthService {

    @Value("${jwt.expire-seconds:604800}")
    private Long expireSeconds;

    private final AdminMapper adminMapper;
    private final JwtUtil jwtUtil;

    public AdminAuthService(AdminMapper adminMapper, JwtUtil jwtUtil) {
        this.adminMapper = adminMapper;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 管理员登录
     */
    public AdminLoginResponse login(AdminLoginRequest request, String clientIp) {
        // 1. 查询管理员
        Admin admin = adminMapper.selectOne(
                new LambdaQueryWrapper<Admin>()
                        .eq(Admin::getUsername, request.getUsername())
        );

        if (admin == null) {
            log.warn("管理员不存在: {}", request.getUsername());
            throw new BizException(ErrorCode.ADMIN_NOT_FOUND);
        }

        // 2. 检查状态
        if (admin.getStatus() != 1) {
            log.warn("管理员已被禁用: {}", request.getUsername());
            throw new BizException(ErrorCode.ADMIN_DISABLED);
        }

        // 3. 验证密码
        if (!BCrypt.checkpw(request.getPassword(), admin.getPassword())) {
            log.warn("管理员密码错误: {}", request.getUsername());
            throw new BizException(ErrorCode.ADMIN_PASSWORD_ERROR);
        }

        // 4. 更新登录信息
        admin.setLastLoginTime(LocalDateTime.now());
        admin.setLastLoginIp(clientIp);
        adminMapper.updateById(admin);

        // 5. 生成 Token
        String token = jwtUtil.generateAdminToken(admin.getId(), admin.getUsername(), admin.getRole());

        // 6. 构建响应
        AdminLoginResponse response = new AdminLoginResponse();
        response.setToken(token);
        response.setExpiresIn(expireSeconds);
        response.setAdminInfo(buildAdminInfo(admin));

        log.info("管理员登录成功: {}", request.getUsername());
        return response;
    }

    /**
     * 构建管理员信息 VO
     */
    private AdminInfoVO buildAdminInfo(Admin admin) {
        AdminInfoVO vo = new AdminInfoVO();
        vo.setId(String.valueOf(admin.getId()));
        vo.setUsername(admin.getUsername());
        vo.setName(admin.getName());
        vo.setRole(admin.getRole());

        // 解析权限
        if (admin.getPermissions() != null) {
            vo.setPermissions(JSON.parseArray(admin.getPermissions(), String.class));
        } else {
            vo.setPermissions(List.of());
        }

        return vo;
    }
}
