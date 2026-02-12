package com.clinic.lottery.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
@TableName("users")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String openid;

    private String unionid;

    private String nickname;

    private String avatarUrl;

    private String phone;

    private Integer gender;

    private Integer points;

    private Integer totalPoints;

    private Integer lotteryChances;

    private Integer totalLotteryCount;

    private Integer winCount;

    private Integer consecutiveLose;

    private String deviceId;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
