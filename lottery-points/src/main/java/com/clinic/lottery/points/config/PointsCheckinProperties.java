package com.clinic.lottery.points.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 签到奖励配置
 */
@Data
@ConfigurationProperties(prefix = "points.checkin")
public class PointsCheckinProperties {

    /**
     * 每日基础积分
     */
    private int dailyPoints = 5;

    /**
     * 每日基础抽奖次数
     */
    private int dailyChances = 0;

    /**
     * 连续签到奖励规则
     */
    private List<BonusRule> consecutiveBonus = new ArrayList<>();

    @Data
    public static class BonusRule {
        /**
         * 连续签到天数门槛
         */
        private int days;

        /**
         * 额外奖励积分
         */
        private int bonusPoints;

        /**
         * 额外奖励抽奖次数
         */
        private int bonusChances;
    }
}
