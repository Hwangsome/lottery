package com.clinic.lottery.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 中奖记录状态枚举
 */
@Getter
@AllArgsConstructor
public enum RecordStatus {

    PENDING(0, "待领取"),
    RECEIVED(1, "已领取/待发货"),
    SHIPPED(2, "已发货"),
    USED(3, "已使用/已核销"),
    EXPIRED(4, "已过期"),
    FAILED(5, "发放失败");

    private final int code;
    private final String desc;

    public static RecordStatus fromCode(int code) {
        for (RecordStatus status : values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return PENDING;
    }
}
