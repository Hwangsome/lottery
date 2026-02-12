package com.clinic.lottery.gateway.config;

import com.clinic.lottery.common.util.JwtUtil;
import com.clinic.lottery.gateway.security.AddAuthHeaderWebFilter;
import com.clinic.lottery.gateway.security.JwtReactiveAuthenticationManager;
import com.clinic.lottery.gateway.security.JwtServerAuthenticationConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * 网关 Spring Security 配置：JWT 鉴权 + 路径权限
 */
@Slf4j
@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class GatewaySecurityConfig {

    private static final String UNAUTHORIZED_JSON = "{\"code\":10002,\"message\":\"未授权，请先登录\",\"data\":null}";
    private static final String FORBIDDEN_JSON = "{\"code\":10003,\"message\":\"权限不足\",\"data\":null}";

    private final JwtUtil jwtUtil;

    @Bean
    public JwtServerAuthenticationConverter jwtServerAuthenticationConverter() {
        return new JwtServerAuthenticationConverter();
    }

    @Bean
    public JwtReactiveAuthenticationManager jwtReactiveAuthenticationManager() {
        return new JwtReactiveAuthenticationManager(jwtUtil);
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        AuthenticationWebFilter jwtAuthFilter = new AuthenticationWebFilter(
                jwtReactiveAuthenticationManager());
        jwtAuthFilter.setServerAuthenticationConverter(jwtServerAuthenticationConverter());
        jwtAuthFilter.setSecurityContextRepository(NoOpServerSecurityContextRepository.getInstance());

        AddAuthHeaderWebFilter addAuthHeaderFilter = new AddAuthHeaderWebFilter();

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .logout(ServerHttpSecurity.LogoutSpec::disable)
                .exceptionHandling(spec -> spec
                        .authenticationEntryPoint(unauthorizedEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler()))
                .authorizeExchange(spec -> spec
                        // 登录与公开接口放行
                        .pathMatchers(
                                "/api/v1/user/login",
                                "/api/v1/admin/login",
                                "/api/v1/lottery/activity",
                                "/api/v1/lottery/prizes",
                                "/api/v1/lottery/records/latest",
                                "/api/v1/lottery/winning-records",
                                "/actuator/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/webjars/**",
                                "/v3/api-docs/**",
                                "/api-docs/**"
                        ).permitAll()
                        // 用户端需 user token
                        .pathMatchers(
                                "/api/v1/user/**",
                                "/api/v1/lottery/**",
                                "/api/v1/points/**"
                        ).hasRole("USER")
                        // 管理端需 admin token
                        .pathMatchers(
                                "/api/v1/verify/**",
                                "/api/v1/admin/**"
                        ).hasRole("ADMIN")
                        .anyExchange().denyAll())
                .addFilterAt(jwtAuthFilter, org.springframework.security.config.web.server.SecurityWebFiltersOrder.AUTHENTICATION)
                .addFilterAfter(addAuthHeaderFilter, org.springframework.security.config.web.server.SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    private ServerAuthenticationEntryPoint unauthorizedEntryPoint() {
        return (exchange, ex) -> {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            DataBuffer buffer = exchange.getResponse().bufferFactory()
                    .wrap(UNAUTHORIZED_JSON.getBytes(StandardCharsets.UTF_8));
            return exchange.getResponse().writeWith(Mono.just(buffer));
        };
    }

    private ServerAccessDeniedHandler accessDeniedHandler() {
        return (exchange, denied) -> {
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            DataBuffer buffer = exchange.getResponse().bufferFactory()
                    .wrap(FORBIDDEN_JSON.getBytes(StandardCharsets.UTF_8));
            return exchange.getResponse().writeWith(Mono.just(buffer));
        };
    }
}
