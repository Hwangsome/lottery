package com.clinic.lottery.prize.service;

import com.clinic.lottery.api.service.UserService;
import com.clinic.lottery.common.constant.ChanceType;
import com.clinic.lottery.common.constant.ErrorCode;
import com.clinic.lottery.common.exception.BizException;
import com.clinic.lottery.common.util.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 抽奖次数管理服务
 */
@Slf4j
@Service
public class ChanceService {

    @DubboReference(check = false, protocol = "dubbo")
    private UserService userService;

    private final StringRedisTemplate redisTemplate;

    public ChanceService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 检查抽奖次数
     */
    public boolean checkChance(Long userId, Long activityId) {
        int chances = getChances(userId, activityId);
        if (chances <= 0) {
            throw new BizException(ErrorCode.LOTTERY_CHANCE_NOT_ENOUGH);
        }
        return true;
    }

    /**
     * 获取抽奖次数（优先从缓存获取）
     */
    public int getChances(Long userId, Long activityId) {
        String key = RedisKeyUtil.lotteryChances(userId, activityId);
        String value = redisTemplate.opsForValue().get(key);
        if (value != null) {
            return Integer.parseInt(value);
        }

        // 从数据库查询
        int chances = userService.getChances(userId);
        // 缓存到 Redis（30分钟过期）
        redisTemplate.opsForValue().set(key, String.valueOf(chances), java.time.Duration.ofMinutes(30));
        return chances;
    }

    /**
     * 扣减抽奖次数
     */
    public boolean deductChance(Long userId, Long activityId) {
        String key = RedisKeyUtil.lotteryChances(userId, activityId);

        // 先尝试在 Redis 中扣减
        Long remaining = redisTemplate.opsForValue().decrement(key);
        if (remaining != null && remaining >= 0) {
            // Redis 扣减成功，异步更新数据库
            boolean success = userService.deductChances(userId, 1);
            if (!success) {
                // 数据库扣减失败，回滚 Redis
                redisTemplate.opsForValue().increment(key);
                return false;
            }
            log.debug("扣减抽奖次数成功，userId={}, activityId={}, remaining={}",
                    userId, activityId, remaining);
            return true;
        }

        // Redis 扣减失败，从数据库重新获取并检查
        int chances = userService.getChances(userId);
        if (chances <= 0) {
            return false;
        }

        // 更新 Redis 缓存
        redisTemplate.opsForValue().set(key, String.valueOf(chances - 1), java.time.Duration.ofMinutes(30));

        // 扣减数据库次数
        return userService.deductChances(userId, 1);
    }

    /**
     * 增加抽奖次数
     */
    public boolean addChance(Long userId, Long activityId, int chances, ChanceType type, String reason) {
        // 先更新数据库
        boolean success = userService.addChances(userId, chances, reason);
        if (success) {
            // 更新缓存
            String key = RedisKeyUtil.lotteryChances(userId, activityId);
            redisTemplate.opsForValue().increment(key, chances);
            log.debug("增加抽奖次数成功，userId={}, activityId={}, chances={}, type={}",
                    userId, activityId, chances, type.getDesc());
        }
        return success;
    }
}
