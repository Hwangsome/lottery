package com.clinic.lottery.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 抽奖次数来源类型枚举
 */
@Getter
@AllArgsConstructor
public enum ChanceType {

    NEW_USER(1, "新用户赠送"),
    DAILY_FREE(2, "每日免费"),
    CONSUMPTION(3, "消费获得"),
    POINTS_EXCHANGE(4, "积分兑换"),
    SHARE(5, "分享获得"),
    CHECKIN(6, "签到获得"),
    LOTTERY_USE(7, "抽奖消耗"),
    ADMIN_ADJUST(8, "管理员调整");

    private final int code;
    private final String desc;

    public static ChanceType fromCode(int code) {
        for (ChanceType type : values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return DAILY_FREE;
    }
}
