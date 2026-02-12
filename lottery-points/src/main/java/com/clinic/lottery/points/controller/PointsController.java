package com.clinic.lottery.points.controller;

import com.clinic.lottery.api.dto.PointsRecordDTO;
import com.clinic.lottery.api.service.PointsService;
import com.clinic.lottery.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 积分服务 Controller
 */
@Slf4j
@RestController
@RequestMapping({"/api/points", "/api/v1/points"})
@Tag(name = "积分服务", description = "积分管理相关接口")
public class PointsController {

    private final PointsService pointsService;

    public PointsController(PointsService pointsService) {
        this.pointsService = pointsService;
    }

    /**
     * 获取用户积分余额
     */
    @GetMapping("/balance")
    @Operation(summary = "获取积分余额", description = "获取当前登录用户的积分余额")
    public Result<Integer> getPointsBalance(@RequestHeader("X-User-Id") Long userId) {
        int balance = pointsService.getPoints(userId);
        log.debug("获取积分余额成功，userId={}, balance={}", userId, balance);
        return Result.success(balance);
    }

    /**
     * 获取积分明细
     */
    @GetMapping("/records")
    @Operation(summary = "获取积分明细", description = "获取用户的积分变动记录")
    public Result<List<PointsRecordDTO>> getPointsRecords(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(name = "type", required = false) Integer type,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
        List<PointsRecordDTO> records = pointsService.getRecords(userId, type, page, pageSize);
        log.debug("获取积分明细成功，userId={}, recordCount={}", userId, records.size());
        return Result.success(records);
    }

    /**
     * 每日签到
     */
    @PostMapping("/checkin")
    @Operation(summary = "每日签到", description = "用户每日签到获取积分")
    public Result<PointsRecordDTO> checkin(@RequestHeader("X-User-Id") Long userId) {
        PointsRecordDTO record = pointsService.checkin(userId);
        log.debug("签到成功，userId={}", userId);
        return Result.success(record);
    }

    /**
     * 检查今日是否已签到
     */
    @GetMapping("/checkin/status")
    @Operation(summary = "检查签到状态", description = "检查用户今日是否已签到")
    public Result<Boolean> checkinStatus(@RequestHeader("X-User-Id") Long userId) {
        boolean hasCheckedIn = pointsService.hasCheckedInToday(userId);
        log.debug("检查签到状态成功，userId={}, hasCheckedIn={}", userId, hasCheckedIn);
        return Result.success(hasCheckedIn);
    }

    /**
     * 获取连续签到天数
     */
    @GetMapping("/checkin/consecutive")
    @Operation(summary = "获取连续签到天数", description = "获取用户连续签到的天数")
    public Result<Integer> getConsecutiveDays(@RequestHeader("X-User-Id") Long userId) {
        int days = pointsService.getConsecutiveDays(userId);
        log.debug("获取连续签到天数成功，userId={}, days={}", userId, days);
        return Result.success(days);
    }

    /**
     * 积分兑换抽奖机会
     */
    @PostMapping("/exchange/chances")
    @Operation(summary = "积分兑换抽奖机会", description = "使用积分兑换抽奖机会")
    public Result<Boolean> exchangeForChances(@RequestHeader("X-User-Id") Long userId,
                                              @RequestParam(name = "times") int times) {
        boolean success = pointsService.exchangeForChances(userId, times);
        log.debug("积分兑换抽奖机会成功，userId={}, times={}", userId, times);
        return Result.success(success);
    }

    /**
     * 获取即将过期的积分
     */
    @GetMapping("/expiring")
    @Operation(summary = "获取即将过期的积分", description = "获取即将在指定天数内过期的积分")
    public Result<Integer> getExpiringSoonPoints(@RequestHeader("X-User-Id") Long userId,
                                                 @RequestParam(name = "days", defaultValue = "30") int days) {
        int expiringPoints = pointsService.getExpiringSoonPoints(userId, days);
        log.debug("获取即将过期的积分成功，userId={}, days={}, points={}", userId, days, expiringPoints);
        return Result.success(expiringPoints);
    }
}
