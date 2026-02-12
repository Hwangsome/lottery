package com.clinic.lottery.api.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 活动详情完整响应 VO
 */
@Data
public class ActivityDetailVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private ActivityInfoVO activity;
    private List<PrizeSimpleVO> prizes;
    private Integer userChances;
    private ActivityRulesVO rules;
}
