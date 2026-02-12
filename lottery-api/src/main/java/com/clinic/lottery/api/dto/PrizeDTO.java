package com.clinic.lottery.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 奖品 DTO
 */
@Data
public class PrizeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long activityId;
    private String name;
    private Integer type;
    private String imageUrl;
    private String description;
    private BigDecimal value;
    private BigDecimal probability;
    private Integer totalStock;
    private Integer remainingStock;
    private Integer dailyLimit;
    private Integer dailySent;
    private Boolean isGuarantee;
    private Integer sortOrder;
    private String couponConfig;
    private String redpackConfig;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
