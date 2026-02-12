package com.clinic.lottery.gateway.security;

import com.clinic.lottery.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

/**
 * 使用 JwtUtil 校验 Token，并构造 GatewayAuthPrincipal 放入 SecurityContext
 */
@Slf4j
@RequiredArgsConstructor
public class JwtReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtUtil jwtUtil;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        Object credentials = authentication.getCredentials();
        if (credentials == null || !(credentials instanceof String)) {
            return Mono.empty();
        }
        String token = (String) credentials;

        if (!jwtUtil.validateToken(token)) {
            log.warn("Token 无效或已过期");
            return Mono.empty();
        }

        String tokenType = jwtUtil.getTokenType(token);
        if (GatewayAuthPrincipal.TYPE_USER.equals(tokenType)) {
            Long userId = jwtUtil.getUserId(token);
            if (userId == null) {
                log.warn("无法从 Token 中获取用户 ID");
                return Mono.empty();
            }
            GatewayAuthPrincipal principal = GatewayAuthPrincipal.user(userId);
            return Mono.just(
                    new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                            principal, null, principal.getAuthorities()));
        }

        if (GatewayAuthPrincipal.TYPE_ADMIN.equals(tokenType)) {
            Long adminId = jwtUtil.getAdminId(token);
            if (adminId == null) {
                log.warn("无法从 Token 中获取管理员 ID");
                return Mono.empty();
            }
            GatewayAuthPrincipal principal = GatewayAuthPrincipal.admin(adminId);
            return Mono.just(
                    new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                            principal, null, principal.getAuthorities()));
        }

        log.warn("Token 类型不支持: {}", tokenType);
        return Mono.empty();
    }
}
