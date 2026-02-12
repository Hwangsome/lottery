package com.clinic.lottery.api.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 抽奖结果 DTO
 */
@Data
public class LotteryResultDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 是否中奖
     */
    private Boolean isWin;

    /**
     * 奖品信息
     */
    private PrizeDTO prize;

    /**
     * 奖品在转盘上的位置索引
     */
    private Integer prizeIndex;

    /**
     * 中奖记录ID
     */
    private Long recordId;

    /**
     * 剩余抽奖次数
     */
    private Integer remainingChances;
}
