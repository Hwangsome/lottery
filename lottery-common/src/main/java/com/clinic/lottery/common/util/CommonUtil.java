package com.clinic.lottery.common.util;

import cn.hutool.core.util.RandomUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 通用工具类
 */
public class CommonUtil {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 获取今日日期字符串
     */
    public static String getTodayStr() {
        return LocalDate.now().format(DATE_FORMATTER);
    }

    /**
     * 格式化日期
     */
    public static String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : null;
    }

    /**
     * 格式化日期时间
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATETIME_FORMATTER) : null;
    }

    /**
     * 生成优惠券码（8位大写字母+数字）
     */
    public static String generateCouponCode() {
        return RandomUtil.randomString("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789", 8);
    }

    /**
     * 生成订单号
     */
    public static String generateOrderNo(String prefix) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        String random = RandomUtil.randomNumbers(4);
        return prefix + timestamp + random;
    }

    /**
     * 手机号脱敏
     */
    public static String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }

    /**
     * 姓名脱敏
     */
    public static String maskName(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        if (name.length() == 1) {
            return "*";
        }
        if (name.length() == 2) {
            return name.charAt(0) + "*";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(name.charAt(0));
        for (int i = 1; i < name.length() - 1; i++) {
            sb.append("*");
        }
        sb.append(name.charAt(name.length() - 1));
        return sb.toString();
    }

    /**
     * 计算消费获得的抽奖次数
     * 100元=1次，500元=3次，1000元=5次
     */
    public static int calculateChancesByAmount(double amount) {
        if (amount >= 1000) {
            return 5;
        } else if (amount >= 500) {
            return 3;
        } else if (amount >= 100) {
            return 1;
        }
        return 0;
    }

    /**
     * 计算消费获得的积分（1元=1积分）
     */
    public static int calculatePointsByAmount(double amount) {
        return (int) Math.floor(amount);
    }
}
