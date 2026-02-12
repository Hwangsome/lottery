package com.clinic.lottery.prize.service;

import com.clinic.lottery.prize.entity.Prize;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * 概率计算引擎
 */
@Slf4j
@Service
public class ProbabilityEngine {

    private static final int GUARANTEE_COUNT = 10;

    /**
     * 计算中奖奖品
     *
     * @param prizes           奖品列表
     * @param consecutiveLose  连续未中奖次数
     * @return 中奖奖品
     */
    public Prize calculate(List<Prize> prizes, int consecutiveLose) {
        // 1. 过滤无库存奖品（remainingStock = -1 表示不限库存）
        List<Prize> available = prizes.stream()
                .filter(p -> p.getRemainingStock() == -1 || p.getRemainingStock() > 0)
                .filter(p -> p.getStatus() == 1)
                .collect(Collectors.toList());

        if (available.isEmpty()) {
            log.warn("没有可用奖品");
            return getThanksPrize(prizes);
        }

        // 2. 检查保底机制（连续10次未中奖，必中保底奖品）
        if (consecutiveLose >= GUARANTEE_COUNT) {
            Prize guaranteePrize = available.stream()
                    .filter(Prize::getIsGuarantee)
                    .findFirst()
                    .orElse(null);

            if (guaranteePrize != null) {
                log.info("触发保底机制，连续未中奖次数={}", consecutiveLose);
                return guaranteePrize;
            }
        }

        // 3. 按概率计算
        double total = available.stream()
                .mapToDouble(p -> p.getProbability().doubleValue())
                .sum();

        if (total <= 0) {
            return getThanksPrize(prizes);
        }

        double random = ThreadLocalRandom.current().nextDouble(0, total);
        double acc = 0;

        for (Prize prize : available) {
            acc += prize.getProbability().doubleValue();
            if (random < acc) {
                log.debug("概率计算结果: random={}, acc={}, prize={}", random, acc, prize.getName());
                return prize;
            }
        }

        // 兜底返回谢谢参与
        return getThanksPrize(prizes);
    }

    /**
     * 获取谢谢参与奖品
     */
    private Prize getThanksPrize(List<Prize> prizes) {
        return prizes.stream()
                .filter(p -> p.getType() == 4) // 类型4为谢谢参与
                .findFirst()
                .orElse(prizes.get(prizes.size() - 1)); // 如果没有，返回最后一个
    }
}
