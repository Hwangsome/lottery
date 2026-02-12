package com.clinic.lottery.common.util;

/**
 * Redis Key 工具类
 */
public class RedisKeyUtil {

    private static final String PREFIX = "lottery:";

    // ==================== 用户相关 ====================

    /**
     * 用户 Token
     */
    public static String userToken(Long userId) {
        return PREFIX + "user:token:" + userId;
    }

    /**
     * 用户信息缓存
     */
    public static String userInfo(Long userId) {
        return PREFIX + "user:info:" + userId;
    }

    // ==================== 抽奖相关 ====================

    /**
     * 抽奖限流 Key（10秒内仅1次）
     */
    public static String lotteryRateLimit(Long userId) {
        return PREFIX + "rate:lottery:" + userId;
    }

    /**
     * 用户抽奖次数
     */
    public static String lotteryChances(Long userId, Long activityId) {
        return PREFIX + "chances:" + activityId + ":" + userId;
    }

    /**
     * 奖品库存
     */
    public static String prizeStock(Long prizeId) {
        return PREFIX + "stock:prize:" + prizeId;
    }

    /**
     * 奖品今日已发放数量
     */
    public static String prizeDailySent(Long prizeId, String date) {
        return PREFIX + "daily:prize:" + prizeId + ":" + date;
    }

    // ==================== 活动相关 ====================

    /**
     * 活动配置缓存
     */
    public static String activityInfo(Long activityId) {
        return PREFIX + "activity:info:" + activityId;
    }

    /**
     * 活动奖品列表缓存
     */
    public static String activityPrizes(Long activityId) {
        return PREFIX + "activity:prizes:" + activityId;
    }

    /**
     * 当前进行中的活动
     */
    public static String currentActivity() {
        return PREFIX + "activity:current";
    }

    // ==================== 签到相关 ====================

    /**
     * 今日签到标记
     */
    public static String checkinToday(Long userId, String date) {
        return PREFIX + "checkin:" + date + ":" + userId;
    }

    /**
     * 用户连续签到天数
     */
    public static String checkinConsecutive(Long userId) {
        return PREFIX + "checkin:consecutive:" + userId;
    }

    // ==================== 分享相关 ====================

    /**
     * 今日分享获得次数
     */
    public static String shareTodayCount(Long userId, String date) {
        return PREFIX + "share:count:" + date + ":" + userId;
    }

    /**
     * 今日被同一好友点击记录
     */
    public static String shareFriendClick(Long userId, Long friendId, String date) {
        return PREFIX + "share:click:" + date + ":" + userId + ":" + friendId;
    }

    // ==================== 保底相关 ====================

    /**
     * 用户连续未中奖次数
     */
    public static String consecutiveLose(Long userId) {
        return PREFIX + "lose:consecutive:" + userId;
    }

    // ==================== 管理员相关 ====================

    /**
     * 管理员 Token
     */
    public static String adminToken(Long adminId) {
        return PREFIX + "admin:token:" + adminId;
    }

    // ==================== 分布式锁 ====================

    /**
     * 抽奖锁
     */
    public static String lotteryLock(Long userId, Long activityId) {
        return PREFIX + "lock:lottery:" + activityId + ":" + userId;
    }

    /**
     * 库存扣减锁
     */
    public static String stockLock(Long prizeId) {
        return PREFIX + "lock:stock:" + prizeId;
    }

    /**
     * 签到锁
     */
    public static String checkinLock(Long userId) {
        return PREFIX + "lock:checkin:" + userId;
    }
}
