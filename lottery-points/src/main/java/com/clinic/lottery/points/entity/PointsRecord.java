package com.clinic.lottery.points.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 积分记录实体
 */
@Data
@TableName("points_records")
public class PointsRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Integer type;

    private Integer changeAmount;

    private Integer balance;

    private String sourceId;

    private LocalDateTime expireTime;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
