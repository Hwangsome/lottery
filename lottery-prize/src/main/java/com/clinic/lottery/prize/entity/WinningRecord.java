package com.clinic.lottery.prize.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 中奖记录实体
 */
@Data
@TableName("winning_records")
public class WinningRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long activityId;

    private Long prizeId;

    private String prizeName;

    private Integer prizeType;

    private BigDecimal prizeValue;

    private Integer status;

    private String couponCode;

    private String qrcodeUrl;

    private LocalDateTime expireTime;

    private LocalDateTime receiveTime;

    private LocalDateTime verifyTime;

    private String verifyUser;

    private String shippingInfo;

    private String redpackInfo;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
