package com.clinic.lottery.gateway.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * 从 SecurityContext 读取当前用户/管理员，将 userId 或 adminId 写入请求头传给下游
 */
@Slf4j
public class AddAuthHeaderWebFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
                .filter(ctx -> ctx.getAuthentication() != null)
                .flatMap(ctx -> {
                    Authentication auth = ctx.getAuthentication();
                    if (!(auth.getPrincipal() instanceof GatewayAuthPrincipal principal)) {
                        return chain.filter(exchange);
                    }
                    ServerHttpRequest.Builder builder = exchange.getRequest().mutate();
                    if (principal.isUser() && principal.getUserId() != null) {
                        builder.header("X-User-Id", String.valueOf(principal.getUserId()));
                        log.debug("注入 X-User-Id: {}", principal.getUserId());
                    } else if (principal.isAdmin() && principal.getAdminId() != null) {
                        builder.header("X-Admin-Id", String.valueOf(principal.getAdminId()));
                        log.debug("注入 X-Admin-Id: {}", principal.getAdminId());
                    }
                    return chain.filter(exchange.mutate().request(builder.build()).build());
                })
                .switchIfEmpty(chain.filter(exchange));
    }
}
