package com.clinic.lottery.points.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalDate;

/**
 * 签到记录实体
 */
@Data
@TableName("checkin_records")
public class CheckinRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private LocalDate checkinDate;

    private Integer consecutiveDays;

    private Integer pointsAwarded;

    private Integer chancesAwarded;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
