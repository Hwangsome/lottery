package com.clinic.lottery.prize.service;

import com.clinic.lottery.api.dto.LotteryResultDTO;
import com.clinic.lottery.api.dto.PrizeDTO;
import com.clinic.lottery.api.dto.WinningRecordDTO;
import com.clinic.lottery.api.service.ActivityService;
import com.clinic.lottery.api.service.UserService;
import com.clinic.lottery.common.constant.ChanceType;
import com.clinic.lottery.common.constant.ErrorCode;
import com.clinic.lottery.common.constant.PrizeType;
import com.clinic.lottery.common.constant.RecordStatus;
import com.clinic.lottery.common.exception.BizException;
import com.clinic.lottery.common.util.CommonUtil;
import com.clinic.lottery.common.util.RedisKeyUtil;
import com.clinic.lottery.prize.entity.Prize;
import com.clinic.lottery.prize.entity.WinningRecord;
import com.clinic.lottery.prize.mapper.PrizeMapper;
import com.clinic.lottery.prize.mapper.WinningRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 抽奖核心服务
 */
@Slf4j
@Service
public class LotteryService {

    @DubboReference(check = false, protocol = "dubbo")
    private ActivityService activityService;

    @DubboReference(check = false, protocol = "dubbo")
    private UserService userService;

    private final PrizeMapper prizeMapper;
    private final WinningRecordMapper winningRecordMapper;
    private final ProbabilityEngine probabilityEngine;
    private final StockService stockService;
    private final ChanceService chanceService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public LotteryService(PrizeMapper prizeMapper, WinningRecordMapper winningRecordMapper,
                          ProbabilityEngine probabilityEngine, StockService stockService,
                          ChanceService chanceService, KafkaTemplate<String, Object> kafkaTemplate) {
        this.prizeMapper = prizeMapper;
        this.winningRecordMapper = winningRecordMapper;
        this.probabilityEngine = probabilityEngine;
        this.stockService = stockService;
        this.chanceService = chanceService;
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * 执行抽奖
     */
    @Transactional(rollbackFor = Exception.class)
    public LotteryResultDTO draw(Long userId, Long activityId) {
        log.info("用户抽奖请求，userId={}, activityId={}", userId, activityId);

        // 1. 校验活动
        validateActivity(activityId);

        // 2. 校验抽奖次数
        chanceService.checkChance(userId, activityId);

        // 3. 获取奖品列表
        List<Prize> prizes = prizeMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Prize>()
                        .eq(Prize::getActivityId, activityId)
                        .eq(Prize::getStatus, 1)
                        .orderByAsc(Prize::getSortOrder)
        );

        if (prizes.isEmpty()) {
            throw new BizException(ErrorCode.PRIZE_STOCK_NOT_ENOUGH);
        }

        // 4. 获取连续未中奖次数
        int consecutiveLose = userService.getConsecutiveLose(userId);

        // 5. 概率计算
        Prize winPrize = probabilityEngine.calculate(prizes, consecutiveLose);

        // 6. 扣减库存
        boolean stockDeducted = stockService.deductStock(winPrize.getId(), winPrize);
        if (!stockDeducted && winPrize.getType() != 4) { // 不是谢谢参与且扣库存失败
            winPrize = prizes.stream()
                    .filter(p -> p.getType() == 4)
                    .findFirst()
                    .orElse(null);
        }

        // 7. 扣减抽奖次数
        boolean chanceDeducted = chanceService.deductChance(userId, activityId);
        if (!chanceDeducted) {
            throw new BizException(ErrorCode.LOTTERY_CHANCE_NOT_ENOUGH);
        }

        // 8. 创建中奖记录
        WinningRecord record = createWinningRecord(userId, activityId, winPrize);

        // 9. 更新用户统计
        updateUserStatistics(userId, winPrize.getType() != 4);

        // 10. 发送 Kafka 消息
        sendWinningEvent(record);

        // 11. 构建响应
        LotteryResultDTO result = buildResult(winPrize, record, userId, activityId);
        log.info("用户抽奖完成，userId={}, activityId={}, prize={}",
                userId, activityId, winPrize.getName());

        return result;
    }

    /**
     * 校验活动
     */
    private void validateActivity(Long activityId) {
        boolean valid = activityService.validateActivity(activityId);
        if (!valid) {
            throw new BizException(ErrorCode.ACTIVITY_NOT_FOUND);
        }
    }

    /**
     * 创建中奖记录
     */
    private WinningRecord createWinningRecord(Long userId, Long activityId, Prize prize) {
        WinningRecord record = new WinningRecord();
        record.setUserId(userId);
        record.setActivityId(activityId);
        record.setPrizeId(prize.getId());
        record.setPrizeName(prize.getName());
        record.setPrizeType(prize.getType());
        record.setPrizeValue(prize.getValue());
        record.setStatus(RecordStatus.PENDING.getCode());

        // 生成优惠券码
        if (prize.getType() == PrizeType.COUPON.getCode()) {
            record.setCouponCode(CommonUtil.generateCouponCode());
        }

        // 设置过期时间（默认30天）
        record.setExpireTime(LocalDateTime.now().plusDays(30));

        winningRecordMapper.insert(record);
        return record;
    }

    /**
     * 更新用户统计
     */
    private void updateUserStatistics(Long userId, boolean isWin) {
        // 增加总抽奖次数
        userService.incrementTotalLotteryCount(userId);

        if (isWin) {
            // 中奖
            userService.incrementWinCount(userId);
            userService.resetConsecutiveLose(userId);
        } else {
            // 未中奖
            userService.incrementConsecutiveLose(userId);
        }
    }

    /**
     * 发送中奖事件
     */
    private void sendWinningEvent(WinningRecord record) {
        try {
            WinningRecordDTO dto = new WinningRecordDTO();
            BeanUtils.copyProperties(record, dto);
            kafkaTemplate.send("lottery-winning", dto);
            log.debug("发送中奖事件成功，recordId={}", record.getId());
        } catch (Exception e) {
            log.error("发送中奖事件失败，recordId={}", record.getId(), e);
        }
    }

    /**
     * 构建抽奖结果
     */
    private LotteryResultDTO buildResult(Prize prize, WinningRecord record, Long userId, Long activityId) {
        LotteryResultDTO result = new LotteryResultDTO();
        result.setIsWin(prize.getType() != 4);
        result.setRecordId(result.getIsWin() ? record.getId() : null);
        result.setPrizeIndex(prize.getSortOrder());

        PrizeDTO prizeDTO = new PrizeDTO();
        BeanUtils.copyProperties(prize, prizeDTO);
        result.setPrize(prizeDTO);

        // 获取剩余抽奖次数
        int remainingChances = chanceService.getChances(userId, activityId);
        result.setRemainingChances(remainingChances);

        return result;
    }
}
