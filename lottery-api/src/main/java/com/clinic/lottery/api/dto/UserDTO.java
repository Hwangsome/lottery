package com.clinic.lottery.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户 DTO
 */
@Data
public class UserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
