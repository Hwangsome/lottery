package com.clinic.lottery.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * 请求日志过滤器
 */
@Slf4j
@Component
public class RequestLog extends AbstractGatewayFilterFactory<RequestLog.Config> {

    public RequestLog() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String requestId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
            String method = request.getMethod().name();
            String path = request.getURI().getPath();
            String query = request.getURI().getQuery();
            String clientIp = getClientIp(request);

            long startTime = System.currentTimeMillis();

            // 添加请求 ID 到请求头
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-Request-Id", requestId)
                    .build();

            log.info("[{}] {} {} {} from {}", requestId, method, path,
                    query != null ? "?" + query : "", clientIp);

            return chain.filter(exchange.mutate().request(mutatedRequest).build())
                    .then(Mono.fromRunnable(() -> {
                        long duration = System.currentTimeMillis() - startTime;
                        int statusCode = exchange.getResponse().getStatusCode() != null
                                ? exchange.getResponse().getStatusCode().value() : 0;
                        log.info("[{}] {} {} - {} {}ms", requestId, method, path, statusCode, duration);
                    }));
        };
    }

    private String getClientIp(ServerHttpRequest request) {
        String ip = request.getHeaders().getFirst("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeaders().getFirst("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddress() != null
                    ? request.getRemoteAddress().getAddress().getHostAddress() : "unknown";
        }
        // 多个代理时取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    public static class Config {
        // 配置属性（如需要）
    }
}
