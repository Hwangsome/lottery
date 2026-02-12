package com.clinic.lottery.admin.controller;

import com.clinic.lottery.api.dto.ActivityDTO;
import com.clinic.lottery.api.service.ActivityService;
import com.clinic.lottery.common.constant.ErrorCode;
import com.clinic.lottery.common.exception.BizException;
import com.clinic.lottery.common.result.PageResult;
import com.clinic.lottery.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 活动管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/admin/activity")
@Tag(name = "活动管理", description = "活动的增删改查操作")
public class AdminActivityController {

    @DubboReference(check = false, protocol = "dubbo")
    private ActivityService activityService;

    @GetMapping("/list")
    @Operation(summary = "活动列表", description = "获取活动列表，支持分页和筛选")
    public Result<PageResult<ActivityDTO>> listActivities(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "status", required = false) Integer status,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
        log.debug("查询活动列表，name={}, status={}, page={}, pageSize={}", name, status, page, pageSize);
        // 简化实现，返回空列表
        PageResult<ActivityDTO> result = PageResult.of(0, page, pageSize, List.of());
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "活动详情", description = "获取活动详细信息")
    public Result<ActivityDTO> getActivity(@PathVariable("id") Long id) {
        log.debug("查询活动详情，id={}", id);
        ActivityDTO activity = activityService.getById(id);
        if (activity == null) {
            throw new BizException(ErrorCode.NOT_FOUND);
        }
        return Result.success(activity);
    }

    @PostMapping("")
    @Operation(summary = "创建活动", description = "创建新的抽奖活动")
    public Result<ActivityDTO> createActivity(@RequestBody ActivityDTO activityDTO) {
        log.debug("创建活动，name={}", activityDTO.getName());
        ActivityDTO created = activityService.createActivity(activityDTO);
        return Result.success(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新活动", description = "更新活动信息")
    public Result<Void> updateActivity(@PathVariable("id") Long id, @RequestBody ActivityDTO activityDTO) {
        log.debug("更新活动，id={}", id);
        activityDTO.setId(id);
        boolean success = activityService.updateActivity(activityDTO);
        if (!success) {
            throw new BizException(ErrorCode.NOT_FOUND);
        }
        return Result.success();
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新活动状态", description = "启用/禁用活动")
    public Result<Void> updateStatus(@PathVariable("id") Long id, @RequestParam(name = "status") Integer status) {
        log.debug("更新活动状态，id={}, status={}", id, status);
        boolean success = activityService.updateStatus(id, status);
        if (!success) {
            throw new BizException(ErrorCode.NOT_FOUND);
        }
        return Result.success();
    }
}
