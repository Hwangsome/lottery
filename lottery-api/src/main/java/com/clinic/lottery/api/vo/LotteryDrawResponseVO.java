package com.clinic.lottery.api.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 抽奖结果 VO
 */
@Data
public class LotteryDrawResponseVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean isWin;
    private PrizeSimpleVO prize;
    private Integer prizeIndex;
    private Long recordId;
    private Integer remainingChances;
}
