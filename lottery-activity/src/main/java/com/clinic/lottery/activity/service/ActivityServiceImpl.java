package com.clinic.lottery.activity.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.clinic.lottery.activity.entity.Activity;
import com.clinic.lottery.activity.entity.Prize;
import com.clinic.lottery.activity.mapper.ActivityMapper;
import com.clinic.lottery.activity.mapper.PrizeMapper;
import com.clinic.lottery.api.dto.ActivityDTO;
import com.clinic.lottery.api.dto.PrizeDTO;
import com.clinic.lottery.api.service.ActivityService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 活动服务 Dubbo 实现
 */
@Slf4j
@Service
@DubboService
public class ActivityServiceImpl implements ActivityService {

    private final ActivityMapper activityMapper;
    private final PrizeMapper prizeMapper;

    public ActivityServiceImpl(ActivityMapper activityMapper, PrizeMapper prizeMapper) {
        this.activityMapper = activityMapper;
        this.prizeMapper = prizeMapper;
    }

    @Override
    public ActivityDTO getCurrentActivity() {
        LocalDateTime now = LocalDateTime.now();
        Activity activity = activityMapper.selectOne(
                new LambdaQueryWrapper<Activity>()
                        .eq(Activity::getStatus, 1)
                        .le(Activity::getStartTime, now)
                        .ge(Activity::getEndTime, now)
                        .orderByDesc(Activity::getId)
                        .last("LIMIT 1")
        );
        return toActivityDTO(activity);
    }

    @Override
    public ActivityDTO getById(Long activityId) {
        Activity activity = activityMapper.selectById(activityId);
        return toActivityDTO(activity);
    }

    @Override
    public List<PrizeDTO> getPrizesByActivityId(Long activityId) {
        List<Prize> prizes = prizeMapper.selectList(
                new LambdaQueryWrapper<Prize>()
                        .eq(Prize::getActivityId, activityId)
                        .eq(Prize::getStatus, 1)
                        .orderByAsc(Prize::getSortOrder)
        );
        return prizes.stream().map(this::toPrizeDTO).collect(Collectors.toList());
    }

    @Override
    public boolean validateActivity(Long activityId) {
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null || activity.getStatus() != 1) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        return !now.isBefore(activity.getStartTime()) && !now.isAfter(activity.getEndTime());
    }

    @Override
    public ActivityDTO createActivity(ActivityDTO activityDTO) {
        Activity activity = new Activity();
        BeanUtils.copyProperties(activityDTO, activity);
        activityMapper.insert(activity);
        return toActivityDTO(activity);
    }

    @Override
    public boolean updateActivity(ActivityDTO activityDTO) {
        Activity activity = new Activity();
        BeanUtils.copyProperties(activityDTO, activity);
        return activityMapper.updateById(activity) > 0;
    }

    @Override
    public boolean updateStatus(Long activityId, Integer status) {
        Activity activity = new Activity();
        activity.setId(activityId);
        activity.setStatus(status);
        return activityMapper.updateById(activity) > 0;
    }

    private ActivityDTO toActivityDTO(Activity activity) {
        if (activity == null) {
            return null;
        }
        ActivityDTO dto = new ActivityDTO();
        BeanUtils.copyProperties(activity, dto);
        return dto;
    }

    private PrizeDTO toPrizeDTO(Prize prize) {
        if (prize == null) {
            return null;
        }
        PrizeDTO dto = new PrizeDTO();
        BeanUtils.copyProperties(prize, dto);
        return dto;
    }
}
