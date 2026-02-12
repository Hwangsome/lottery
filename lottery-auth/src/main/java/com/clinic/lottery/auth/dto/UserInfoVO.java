package com.clinic.lottery.auth.dto;

import lombok.Data;

/**
 * 用户信息 VO
 */
@Data
public class UserInfoVO {

    private String id;
    private String nickname;
    private String avatarUrl;
    private String phone;
    private Integer points;
    private Integer lotteryChances;
    private Integer totalLotteryCount;
    private Integer winCount;
}
