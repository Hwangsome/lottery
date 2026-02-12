package com.clinic.lottery.api.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 活动规则 VO
 */
@Data
public class ActivityRulesVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer dailyFreeChances;
    private Integer shareChances;
    private Integer dailyShareLimit;
}
