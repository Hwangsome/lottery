package com.clinic.lottery.api.service;

import com.clinic.lottery.api.dto.ActivityDTO;
import com.clinic.lottery.api.dto.PrizeDTO;

import java.util.List;

/**
 * 活动服务 Dubbo 接口
 */
public interface ActivityService {

    /**
     * 获取当前进行中的活动
     */
    ActivityDTO getCurrentActivity();

    /**
     * 根据 ID 获取活动
     */
    ActivityDTO getById(Long activityId);

    /**
     * 获取活动的奖品列表
     */
    List<PrizeDTO> getPrizesByActivityId(Long activityId);

    /**
     * 校验活动是否有效
     */
    boolean validateActivity(Long activityId);

    /**
     * 创建活动
     */
    ActivityDTO createActivity(ActivityDTO activityDTO);

    /**
     * 更新活动
     */
    boolean updateActivity(ActivityDTO activityDTO);

    /**
     * 更新活动状态
     */
    boolean updateStatus(Long activityId, Integer status);
}
