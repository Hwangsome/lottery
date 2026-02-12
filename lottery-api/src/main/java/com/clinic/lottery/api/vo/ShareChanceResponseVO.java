package com.clinic.lottery.api.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 分享增加抽奖次数响应
 */
@Data
public class ShareChanceResponseVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer awardedChances;
    private Integer remainingChances;
    private Integer todayShareCount;
    private Integer dailyShareLimit;
}
