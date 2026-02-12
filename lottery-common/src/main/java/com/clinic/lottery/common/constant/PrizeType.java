package com.clinic.lottery.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 奖品类型枚举
 */
@Getter
@AllArgsConstructor
public enum PrizeType {

    COUPON(1, "优惠券"),
    PHYSICAL(2, "实物奖品"),
    REDPACK(3, "现金红包"),
    THANKS(4, "谢谢参与");

    private final int code;
    private final String desc;

    public static PrizeType fromCode(int code) {
        for (PrizeType type : values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return THANKS;
    }
}
