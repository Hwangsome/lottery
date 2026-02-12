package com.clinic.lottery.api.service;

import com.clinic.lottery.api.dto.PointsRecordDTO;

import java.util.List;

/**
 * 积分服务 Dubbo 接口
 */
public interface PointsService {

    /**
     * 获取用户积分
     */
    int getPoints(Long userId);

    /**
     * 增加积分
     */
    boolean addPoints(Long userId, int points, int type, String sourceId, String remark);

    /**
     * 扣减积分
     */
    boolean deductPoints(Long userId, int points, int type, String sourceId, String remark);

    /**
     * 获取积分明细
     */
    List<PointsRecordDTO> getRecords(Long userId, Integer type, int page, int pageSize);

    /**
     * 签到
     */
    PointsRecordDTO checkin(Long userId);

    /**
     * 检查今日是否已签到
     */
    boolean hasCheckedInToday(Long userId);

    /**
     * 获取连续签到天数
     */
    int getConsecutiveDays(Long userId);

    /**
     * 积分兑换抽奖次数
     */
    boolean exchangeForChances(Long userId, int times);

    /**
     * 获取即将过期的积分
     */
    int getExpiringSoonPoints(Long userId, int days);
}
