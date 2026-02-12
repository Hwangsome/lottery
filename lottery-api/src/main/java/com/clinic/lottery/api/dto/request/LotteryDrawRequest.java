package com.clinic.lottery.api.dto.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 抽奖请求
 */
@Data
public class LotteryDrawRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long activityId;
}
