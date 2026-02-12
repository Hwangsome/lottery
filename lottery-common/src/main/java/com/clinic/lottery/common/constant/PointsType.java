package com.clinic.lottery.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 积分类型枚举
 */
@Getter
@AllArgsConstructor
public enum PointsType {

    CONSUMPTION(1, "消费获得"),
    CHECKIN(2, "签到获得"),
    CHECKIN_BONUS(3, "连续签到奖励"),
    PROFILE_COMPLETE(4, "完善资料"),
    APPOINTMENT(5, "预约就诊"),
    REVIEW(6, "评价服务"),
    INVITE(7, "邀请用户"),
    EXCHANGE_LOTTERY(8, "兑换抽奖次数"),
    EXCHANGE_COUPON(9, "兑换优惠券"),
    EXPIRED(10, "积分过期"),
    ADMIN_ADJUST(11, "管理员调整");

    private final int code;
    private final String desc;

    public static PointsType fromCode(int code) {
        for (PointsType type : values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return CONSUMPTION;
    }
}
