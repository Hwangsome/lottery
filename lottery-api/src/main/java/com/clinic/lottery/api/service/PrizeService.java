package com.clinic.lottery.api.service;

import com.clinic.lottery.api.dto.LotteryResultDTO;
import com.clinic.lottery.api.dto.PrizeDTO;
import com.clinic.lottery.api.dto.WinningRecordDTO;

import java.util.List;

/**
 * 奖品/抽奖服务 Dubbo 接口
 */
public interface PrizeService {

    /**
     * 根据 ID 获取奖品
     */
    PrizeDTO getById(Long prizeId);

    /**
     * 获取活动的奖品列表
     */
    List<PrizeDTO> getByActivityId(Long activityId);

    /**
     * 获取当前活动的奖品列表
     */
    List<PrizeDTO> getCurrentActivityPrizes();

    /**
     * 扣减奖品库存
     */
    boolean deductStock(Long prizeId);

    /**
     * 获取奖品剩余库存
     */
    int getRemainingStock(Long prizeId);

    /**
     * 创建中奖记录
     */
    WinningRecordDTO createWinningRecord(Long userId, Long activityId, Long prizeId);

    /**
     * 获取用户中奖记录
     */
    List<WinningRecordDTO> getUserRecords(Long userId, Integer status, int page, int pageSize);

    /**
     * 获取中奖记录详情
     */
    WinningRecordDTO getRecordById(Long recordId);

    /**
     * 领取实物奖品
     */
    WinningRecordDTO receivePhysicalPrize(Long userId, Long recordId, String shippingInfo);

    /**
     * 更新中奖记录状态
     */
    boolean updateRecordStatus(Long recordId, Integer status);

    /**
     * 获取最新中奖记录（用于滚动展示）
     */
    List<WinningRecordDTO> getLatestWinningRecords(int limit);

    /**
     * 核销优惠券
     */
    WinningRecordDTO verifyCoupon(String couponCode, String verifyUser);
}
