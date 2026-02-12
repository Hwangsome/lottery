package com.clinic.lottery.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 错误码枚举
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 成功
    SUCCESS(0, "success"),

    // 通用错误 10xxx
    PARAM_ERROR(10001, "参数错误"),
    UNAUTHORIZED(10002, "未授权，请先登录"),
    FORBIDDEN(10003, "权限不足"),
    NOT_FOUND(10004, "资源不存在"),
    METHOD_NOT_ALLOWED(10005, "请求方法不支持"),
    TOO_MANY_REQUESTS(10006, "请求过于频繁"),
    INTERNAL_ERROR(10007, "系统内部错误"),

    // 用户相关 11xxx
    USER_NOT_FOUND(11001, "用户不存在"),
    USER_DISABLED(11002, "用户已被禁用"),
    PHONE_BINDIED(11003, "手机号已绑定"),
    WECHAT_LOGIN_FAILED(11004, "微信登录失败"),
    TOKEN_INVALID(11005, "Token无效或已过期"),
    TOKEN_EXPIRED(11006, "Token已过期"),

    // 抽奖相关 20xxx
    LOTTERY_CHANCE_NOT_ENOUGH(20001, "抽奖次数不足"),
    ACTIVITY_NOT_STARTED(20002, "活动未开始"),
    ACTIVITY_ENDED(20003, "活动已结束"),
    ACTIVITY_NOT_FOUND(20004, "活动不存在"),
    PRIZE_STOCK_NOT_ENOUGH(20005, "奖品库存不足"),
    LOTTERY_RATE_LIMIT(20006, "操作过于频繁，请稍后再试"),
    SHARE_LIMIT_REACHED(20007, "今日分享次数已达上限"),
    SHARE_SAME_USER(20008, "同一好友今日已计算过"),

    // 积分相关 30xxx
    POINTS_NOT_ENOUGH(30001, "积分不足"),
    ALREADY_CHECKED_IN(30002, "今日已签到"),
    EXCHANGE_FAILED(30003, "兑换失败"),

    // 奖品核销相关 40xxx
    COUPON_NOT_FOUND(40001, "优惠券不存在"),
    COUPON_USED(40002, "优惠券已使用"),
    COUPON_EXPIRED(40003, "优惠券已过期"),
    COUPON_NOT_YOURS(40004, "优惠券不属于当前用户"),
    PRIZE_ALREADY_RECEIVED(40005, "奖品已领取"),
    SHIPPING_INFO_REQUIRED(40006, "请填写收货信息"),

    // 管理后台相关 50xxx
    ADMIN_NOT_FOUND(50001, "管理员不存在"),
    ADMIN_PASSWORD_ERROR(50002, "密码错误"),
    ADMIN_DISABLED(50003, "管理员已被禁用"),

    // 系统错误 90xxx
    SYSTEM_ERROR(90001, "系统错误"),
    SERVICE_UNAVAILABLE(90002, "服务暂不可用"),
    DATABASE_ERROR(90003, "数据库错误"),
    REDIS_ERROR(90004, "缓存服务错误"),
    RPC_ERROR(90005, "远程调用失败");

    private final int code;
    private final String message;
}
