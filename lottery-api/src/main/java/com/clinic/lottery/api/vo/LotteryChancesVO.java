package com.clinic.lottery.api.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 抽奖次数信息 VO
 */
@Data
public class LotteryChancesVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer totalChances;
    private Boolean todayFreeUsed;
    private Integer todayShareCount;
    private Integer todayShareLimit;
    private Boolean canGetFreeChance;
    private Boolean canShareForChance;
}
