package com.clinic.lottery.activity.controller;

import com.clinic.lottery.api.dto.ActivityDTO;
import com.clinic.lottery.api.dto.PrizeDTO;
import com.clinic.lottery.api.service.ActivityService;
import com.clinic.lottery.api.service.UserService;
import com.clinic.lottery.api.vo.ActivityDetailVO;
import com.clinic.lottery.api.vo.ActivityInfoVO;
import com.clinic.lottery.api.vo.ActivityRulesVO;
import com.clinic.lottery.api.vo.PrizeSimpleVO;
import com.clinic.lottery.common.constant.ErrorCode;
import com.clinic.lottery.common.exception.BizException;
import com.clinic.lottery.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 活动控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/lottery")
@Tag(name = "活动管理", description = "活动信息相关接口")
public class ActivityController {

    private final ActivityService activityService;

    @DubboReference(check = false, protocol = "dubbo")
    private UserService userService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping("/activity")
    @Operation(summary = "获取活动信息", description = "获取当前进行中的活动及奖品列表")
    public Result<ActivityDetailVO> getActivity(
            @RequestParam(name = "activityId", required = false) Long activityId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        log.info("获取活动信息，activityId={}, userId={}", activityId, userId);

        // 获取活动
        ActivityDTO activity;
        if (activityId != null) {
            activity = activityService.getById(activityId);
        } else {
            activity = activityService.getCurrentActivity();
        }

        if (activity == null) {
            throw new BizException(ErrorCode.ACTIVITY_NOT_FOUND);
        }

        // 获取奖品列表
        List<PrizeDTO> prizes = activityService.getPrizesByActivityId(activity.getId());

        // 构建活动信息 VO
        ActivityInfoVO activityInfo = toActivityInfoVO(activity);

        // 构建奖品列表 VO
        List<PrizeSimpleVO> prizeList = prizes.stream()
                .map(this::toPrizeSimpleVO)
                .collect(Collectors.toList());

        // 获取用户抽奖次数
        Integer userChances = 0;
        if (userId != null) {
            userChances = userService.getChances(userId);
        }

        // 构建规则信息 VO
        ActivityRulesVO rules = toActivityRulesVO(activity);

        // 构建响应
        ActivityDetailVO data = new ActivityDetailVO();
        data.setActivity(activityInfo);
        data.setPrizes(prizeList);
        data.setUserChances(userChances);
        data.setRules(rules);

        return Result.success(data);
    }

    private ActivityInfoVO toActivityInfoVO(ActivityDTO activity) {
        ActivityInfoVO vo = new ActivityInfoVO();
        vo.setId(String.valueOf(activity.getId()));
        vo.setName(activity.getName());
        vo.setDescription(activity.getDescription());
        vo.setBannerUrl(activity.getBannerUrl());
        vo.setStartTime(activity.getStartTime());
        vo.setEndTime(activity.getEndTime());
        vo.setStatus(activity.getStatus());
        return vo;
    }

    private PrizeSimpleVO toPrizeSimpleVO(PrizeDTO prize) {
        PrizeSimpleVO vo = new PrizeSimpleVO();
        vo.setId(String.valueOf(prize.getId()));
        vo.setName(prize.getName());
        vo.setType(prize.getType());
        vo.setImageUrl(prize.getImageUrl());
        vo.setValue(prize.getValue());
        vo.setSortOrder(prize.getSortOrder());
        return vo;
    }

    private ActivityRulesVO toActivityRulesVO(ActivityDTO activity) {
        ActivityRulesVO vo = new ActivityRulesVO();
        vo.setDailyFreeChances(activity.getDailyFreeChances());
        vo.setShareChances(activity.getShareChances());
        vo.setDailyShareLimit(activity.getDailyShareLimit());
        return vo;
    }
}
