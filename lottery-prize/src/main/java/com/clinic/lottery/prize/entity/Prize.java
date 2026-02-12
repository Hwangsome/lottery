package com.clinic.lottery.prize.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 奖品实体
 */
@Data
@TableName("prizes")
public class Prize {

    @TableId(type = IdType.AUTO)
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

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
