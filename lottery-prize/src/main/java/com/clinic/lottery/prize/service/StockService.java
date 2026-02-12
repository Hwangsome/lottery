package com.clinic.lottery.prize.service;

import com.clinic.lottery.common.constant.ErrorCode;
import com.clinic.lottery.common.exception.BizException;
import com.clinic.lottery.common.util.RedisKeyUtil;
import com.clinic.lottery.prize.entity.Prize;
import com.clinic.lottery.prize.mapper.PrizeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 库存管理服务
 */
@Slf4j
@Service
public class StockService {

    private final PrizeMapper prizeMapper;
    private final StringRedisTemplate redisTemplate;

    public StockService(PrizeMapper prizeMapper, StringRedisTemplate redisTemplate) {
        this.prizeMapper = prizeMapper;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 扣减奖品库存
     * 先尝试 Redis 扣减，失败则回退
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deductStock(Long prizeId, Prize prize) {
        // 无限库存直接返回
        if (prize.getTotalStock() == -1) {
            return true;
        }

        // 检查每日发放上限
        if (prize.getDailyLimit() > 0) {
            String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
            String dailyKey = RedisKeyUtil.prizeDailySent(prizeId, today);
            String dailySentValue = redisTemplate.opsForValue().get(dailyKey);
            if (dailySentValue != null) {
                int dailySent = Integer.parseInt(dailySentValue);
                if (dailySent >= prize.getDailyLimit()) {
                    log.warn("奖品每日发放已达上限，prizeId={}, limit={}, sent={}",
                            prizeId, prize.getDailyLimit(), dailySent);
                    return false;
                }
            }
        }

        // 尝试 Redis 扣减
        String stockKey = RedisKeyUtil.prizeStock(prizeId);
        Long remaining = redisTemplate.opsForValue().decrement(stockKey);

        if (remaining == null || remaining < 0) {
            // Redis 扣减失败，回滚
            if (remaining != null && remaining < 0) {
                redisTemplate.opsForValue().increment(stockKey);
            }
            log.warn("Redis 库存扣减失败，prizeId={}", prizeId);
            return false;
        }

        // 更新数据库库存（使用乐观锁）
        int rows = prizeMapper.deductStock(prizeId);
        if (rows == 0) {
            // 数据库扣减失败，回滚 Redis
            redisTemplate.opsForValue().increment(stockKey);
            log.warn("数据库库存扣减失败，prizeId={}", prizeId);
            return false;
        }

        // 增加每日发放计数
        if (prize.getDailyLimit() > 0) {
            String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
            String dailyKey = RedisKeyUtil.prizeDailySent(prizeId, today);
            redisTemplate.opsForValue().increment(dailyKey);
            // 设置过期时间（24小时后自动过期）
            if (!redisTemplate.hasKey(dailyKey)) {
                redisTemplate.expire(dailyKey, java.time.Duration.ofHours(24));
            }
        }

        log.debug("库存扣减成功，prizeId={}, remaining={}", prizeId, remaining);
        return true;
    }

    /**
     * 初始化奖品库存到 Redis
     */
    public void initStock(Long prizeId) {
        Prize prize = prizeMapper.selectById(prizeId);
        if (prize != null && prize.getTotalStock() != -1) {
            String key = RedisKeyUtil.prizeStock(prizeId);
            redisTemplate.opsForValue().set(key, String.valueOf(prize.getRemainingStock()));
            log.debug("初始化奖品库存到 Redis，prizeId={}, stock={}", prizeId, prize.getRemainingStock());
        }
    }

    /**
     * 获取剩余库存
     */
    public int getRemainingStock(Long prizeId) {
        String key = RedisKeyUtil.prizeStock(prizeId);
        String value = redisTemplate.opsForValue().get(key);
        if (value != null) {
            return Integer.parseInt(value);
        }

        // 从数据库查询
        Prize prize = prizeMapper.selectById(prizeId);
        if (prize != null) {
            return prize.getRemainingStock();
        }
        return 0;
    }
}
