package com.clinic.lottery.api.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 奖品简要信息 VO（列表项）
 */
@Data
public class PrizeSimpleVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private Integer type;
    private String imageUrl;
    private BigDecimal value;
    private Integer sortOrder;
}
