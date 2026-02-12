package com.clinic.lottery.activity.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 活动实体
 */
@Data
@TableName("activities")
public class Activity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String description;

    private String bannerUrl;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer dailyFreeChances;

    private Integer newUserChances;

    private Integer shareChances;

    private Integer dailyShareLimit;

    private Integer guaranteeCount;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
