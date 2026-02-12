package com.clinic.lottery.api.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户信息 VO（用于 /info 接口）
 */
@Data
public class UserInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String nickname;
    private String avatarUrl;
    private String phone;
    private Integer points;
    private Integer lotteryChances;
    private Integer totalLotteryCount;
    private Integer winCount;
}
