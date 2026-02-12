package com.clinic.lottery.gateway.filter;

import com.clinic.lottery.common.util.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * 抽奖限流过滤器
 * 同一用户 10 秒内仅允许 1 次抽奖
 */
@Slf4j
@Component
public class RateLimitFilter extends AbstractGatewayFilterFactory<RateLimitFilter.Config> {

    private final ReactiveRedisTemplate<String, String> redisTemplate;

    private static final int RATE_LIMIT_SECONDS = 10;
    private static final String LOTTERY_DRAW_PATH = "/api/v1/lottery/draw";

    public RateLimitFilter(ReactiveRedisTemplate<String, String> redisTemplate) {
        super(Config.class);
        this.redisTemplate = redisTemplate;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();

            // 只对抽奖接口进行限流
            if (!LOTTERY_DRAW_PATH.equals(path)) {
                return chain.filter(exchange);
            }

            // 获取用户 ID
            String userIdHeader = request.getHeaders().getFirst("X-User-Id");
            if (userIdHeader == null) {
                return chain.filter(exchange);
            }

            Long userId = Long.parseLong(userIdHeader);
            String rateLimitKey = RedisKeyUtil.lotteryRateLimit(userId);

            return redisTemplate.opsForValue()
                    .setIfAbsent(rateLimitKey, "1", Duration.ofSeconds(RATE_LIMIT_SECONDS))
                    .flatMap(success -> {
                        if (Boolean.TRUE.equals(success)) {
                            // 设置成功，允许请求
                            log.debug("用户 {} 抽奖限流检查通过", userId);
                            return chain.filter(exchange);
                        } else {
                            // 设置失败，说明在限流时间内
                            log.warn("用户 {} 抽奖请求被限流", userId);
                            return rateLimited(exchange.getResponse());
                        }
                    });
        };
    }

    private Mono<Void> rateLimited(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        String body = "{\"code\":20006,\"message\":\"操作过于频繁，请稍后再试\",\"data\":null}";
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }

    public static class Config {
        // 配置属性（如需要）
    }
}
