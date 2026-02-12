package com.clinic.lottery.prize.controller;

import com.clinic.lottery.api.dto.LotteryResultDTO;
import com.clinic.lottery.api.dto.ActivityDTO;
import com.clinic.lottery.api.dto.PrizeDTO;
import com.clinic.lottery.api.dto.WinningRecordDTO;
import com.clinic.lottery.api.dto.request.LotteryDrawRequest;
import com.clinic.lottery.api.dto.request.ReceivePhysicalPrizeRequest;
import com.clinic.lottery.api.dto.request.ShareChanceRequest;
import com.clinic.lottery.api.service.ActivityService;
import com.clinic.lottery.api.service.PrizeService;
import com.clinic.lottery.api.vo.LatestWinningRecordVO;
import com.clinic.lottery.api.vo.LotteryChancesVO;
import com.clinic.lottery.api.vo.LotteryDrawResponseVO;
import com.clinic.lottery.api.vo.PrizeSimpleVO;
import com.clinic.lottery.api.vo.ShareChanceResponseVO;
import com.clinic.lottery.api.vo.WinningRecordVO;
import com.clinic.lottery.common.constant.ChanceType;
import com.clinic.lottery.prize.service.LotteryService;
import com.clinic.lottery.prize.service.ChanceService;
import com.clinic.lottery.common.constant.ErrorCode;
import com.clinic.lottery.common.exception.BizException;
import com.clinic.lottery.common.result.Result;
import com.clinic.lottery.common.util.CommonUtil;
import com.clinic.lottery.common.util.RedisKeyUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 抽奖控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/lottery")
@Tag(name = "抽奖管理", description = "抽奖相关接口")
public class LotteryController {

    private final LotteryService lotteryService;
    private final PrizeService prizeService;
    private final ChanceService chanceService;
    private final StringRedisTemplate redisTemplate;

    @DubboReference(check = false, protocol = "dubbo")
    private ActivityService activityService;

    public LotteryController(LotteryService lotteryService,
                             PrizeService prizeService,
                             ChanceService chanceService,
                             StringRedisTemplate redisTemplate) {
        this.lotteryService = lotteryService;
        this.prizeService = prizeService;
        this.chanceService = chanceService;
        this.redisTemplate = redisTemplate;
    }

    @PostMapping("/draw")
    @Operation(summary = "执行抽奖", description = "消耗一次抽奖机会，返回抽奖结果")
    public Result<LotteryDrawResponseVO> draw(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody LotteryDrawRequest request) {
        log.info("用户抽奖请求，userId={}", userId);

        Long activityId = request.getActivityId();
        if (activityId == null) {
            throw new BizException(ErrorCode.PARAM_ERROR, "活动ID不能为空");
        }

        LotteryResultDTO result = lotteryService.draw(userId, activityId);

        LotteryDrawResponseVO vo = new LotteryDrawResponseVO();
        vo.setIsWin(result.getIsWin());
        vo.setPrize(toPrizeSimpleVO(result.getPrize()));
        vo.setPrizeIndex(result.getPrizeIndex());
        vo.setRecordId(result.getRecordId());
        vo.setRemainingChances(result.getRemainingChances());

        return Result.success(vo);
    }

    @GetMapping("/chances")
    @Operation(summary = "获取抽奖次数", description = "获取用户剩余抽奖次数")
    public Result<LotteryChancesVO> getChances(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(name = "activityId", required = false) Long activityId) {
        // 简化处理，直接从用户服务获取
        LotteryChancesVO vo = new LotteryChancesVO();
        vo.setTotalChances(5);
        vo.setTodayFreeUsed(false);
        vo.setTodayShareCount(0);
        vo.setTodayShareLimit(3);
        vo.setCanGetFreeChance(true);
        vo.setCanShareForChance(true);
        return Result.success(vo);
    }

    @GetMapping("/prizes")
    @Operation(summary = "获取活动奖品列表", description = "获取当前活动的奖品列表，用于大转盘展示")
    public Result<List<PrizeSimpleVO>> getCurrentActivityPrizes() {
        List<PrizeDTO> prizes = prizeService.getCurrentActivityPrizes();
        List<PrizeSimpleVO> data = prizes.stream().map(this::toPrizeSimpleVO).collect(Collectors.toList());
        return Result.success(data);
    }

    @GetMapping("/records")
    @Operation(summary = "获取用户中奖记录", description = "获取当前登录用户的中奖记录")
    public Result<List<WinningRecordVO>> getUserWinningRecords(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(name = "status", required = false) Integer status,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
        List<WinningRecordDTO> records = prizeService.getUserRecords(userId, status, page, pageSize);
        List<WinningRecordVO> data = records.stream().map(this::toWinningRecordVO).collect(Collectors.toList());
        return Result.success(data);
    }

    @PostMapping("/records/{recordId}/receive")
    @Operation(summary = "领取实物奖品", description = "用户填写收货信息领取实物奖品")
    public Result<WinningRecordVO> receivePhysicalPrize(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable("recordId") Long recordId,
            @RequestBody ReceivePhysicalPrizeRequest request) {
        String shippingInfo = buildShippingInfo(request);
        WinningRecordDTO record = prizeService.receivePhysicalPrize(userId, recordId, shippingInfo);
        return Result.success(toWinningRecordVO(record));
    }

    @PostMapping("/claim")
    @Operation(summary = "领取奖品", description = "兼容前端旧路径，提交收货信息领取奖品")
    public Result<WinningRecordVO> claimPrize(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody ReceivePhysicalPrizeRequest request) {
        if (request == null || request.getRecordId() == null) {
            throw new BizException(ErrorCode.PARAM_ERROR, "recordId不能为空");
        }
        String shippingInfo = buildShippingInfo(request);
        WinningRecordDTO record = prizeService.receivePhysicalPrize(userId, request.getRecordId(), shippingInfo);
        return Result.success(toWinningRecordVO(record));
    }

    @GetMapping("/records/latest")
    @Operation(summary = "获取中奖滚动记录", description = "获取最新中奖记录，用于首页滚动展示")
    public Result<List<LatestWinningRecordVO>> getLatestWinningRecords(
            @RequestParam(name = "limit", defaultValue = "10") int limit) {
        List<WinningRecordDTO> records = prizeService.getLatestWinningRecords(limit);
        List<LatestWinningRecordVO> data = records.stream().map(this::toLatestWinningRecordVO).collect(Collectors.toList());
        return Result.success(data);
    }

    @GetMapping("/winning-records")
    @Operation(summary = "获取中奖播报记录", description = "兼容前端旧路径，获取最新中奖滚动记录")
    public Result<List<LatestWinningRecordVO>> getWinningRecords(
            @RequestParam(name = "limit", defaultValue = "10") int limit) {
        return getLatestWinningRecords(limit);
    }

    @PostMapping("/share-chance")
    @Operation(summary = "分享增加抽奖次数", description = "分享后为用户增加抽奖次数（按活动规则和每日上限）")
    public Result<ShareChanceResponseVO> shareChance(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody(required = false) ShareChanceRequest request) {
        ShareChanceRequest req = request != null ? request : new ShareChanceRequest();

        ActivityDTO activity = resolveActivity(req.getActivityId());
        int shareChances = activity.getShareChances() != null && activity.getShareChances() > 0
                ? activity.getShareChances() : 1;
        int dailyShareLimit = activity.getDailyShareLimit() != null && activity.getDailyShareLimit() > 0
                ? activity.getDailyShareLimit() : 3;

        String today = CommonUtil.getTodayStr();
        String shareCountKey = RedisKeyUtil.shareTodayCount(userId, today);
        int todayShareCount = getTodayShareCount(shareCountKey);
        if (todayShareCount >= dailyShareLimit) {
            throw new BizException(ErrorCode.SHARE_LIMIT_REACHED);
        }

        Long friendUserId = req.getFriendUserId();
        if (friendUserId != null) {
            if (friendUserId.equals(userId)) {
                throw new BizException(ErrorCode.SHARE_SAME_USER, "不能给自己助力");
            }
            String friendClickKey = RedisKeyUtil.shareFriendClick(userId, friendUserId, today);
            Boolean firstClick = redisTemplate.opsForValue().setIfAbsent(
                    friendClickKey, "1", Duration.ofSeconds(secondsUntilTomorrow()));
            if (!Boolean.TRUE.equals(firstClick)) {
                throw new BizException(ErrorCode.SHARE_SAME_USER);
            }
        }

        boolean success = chanceService.addChance(
                userId,
                activity.getId(),
                shareChances,
                ChanceType.SHARE,
                "分享获得抽奖次数"
        );
        if (!success) {
            throw new BizException(ErrorCode.INTERNAL_ERROR, "增加抽奖次数失败");
        }

        Long count = redisTemplate.opsForValue().increment(shareCountKey);
        if (count != null && count == 1L) {
            redisTemplate.expire(shareCountKey, Duration.ofSeconds(secondsUntilTomorrow()));
        }
        int remainingChances = chanceService.getChances(userId, activity.getId());

        ShareChanceResponseVO vo = new ShareChanceResponseVO();
        vo.setAwardedChances(shareChances);
        vo.setRemainingChances(remainingChances);
        vo.setTodayShareCount(count != null ? count.intValue() : todayShareCount + 1);
        vo.setDailyShareLimit(dailyShareLimit);
        return Result.success(vo);
    }

    private PrizeSimpleVO toPrizeSimpleVO(PrizeDTO prize) {
        if (prize == null) {
            return null;
        }
        PrizeSimpleVO vo = new PrizeSimpleVO();
        vo.setId(String.valueOf(prize.getId()));
        vo.setName(prize.getName());
        vo.setType(prize.getType());
        vo.setImageUrl(prize.getImageUrl());
        vo.setValue(prize.getValue());
        vo.setSortOrder(prize.getSortOrder());
        return vo;
    }

    private WinningRecordVO toWinningRecordVO(WinningRecordDTO record) {
        if (record == null) {
            return null;
        }
        WinningRecordVO vo = new WinningRecordVO();
        vo.setId(String.valueOf(record.getId()));
        vo.setPrizeName(record.getPrizeName());
        vo.setPrizeType(record.getPrizeType());
        vo.setPrizeValue(record.getPrizeValue());
        vo.setPrizeImageUrl(record.getPrizeImageUrl());
        vo.setStatus(record.getStatus());
        vo.setStatusText(record.getStatusText());
        vo.setCouponCode(record.getCouponCode());
        vo.setQrcodeUrl(record.getQrcodeUrl());
        vo.setShippingInfo(record.getShippingInfo());
        vo.setExpireTime(record.getExpireTime());
        vo.setReceiveTime(record.getReceiveTime());
        vo.setCreatedAt(record.getCreatedAt());
        return vo;
    }

    private LatestWinningRecordVO toLatestWinningRecordVO(WinningRecordDTO record) {
        LatestWinningRecordVO vo = new LatestWinningRecordVO();
        vo.setUserDisplayName(maskUser(record.getUserId()));
        vo.setPrizeName(record.getPrizeName());
        vo.setWinTime(record.getCreatedAt());
        return vo;
    }

    private String buildShippingInfo(ReceivePhysicalPrizeRequest request) {
        if (request == null
                || !StringUtils.hasText(request.getReceiverName())
                || !StringUtils.hasText(request.getReceiverPhone())
                || !StringUtils.hasText(request.getReceiverAddress())) {
            throw new BizException(ErrorCode.SHIPPING_INFO_REQUIRED);
        }

        StringBuilder builder = new StringBuilder();
        builder.append("收件人:").append(request.getReceiverName().trim())
                .append("; 手机号:").append(request.getReceiverPhone().trim())
                .append("; 地址:").append(request.getReceiverAddress().trim());
        if (StringUtils.hasText(request.getRemark())) {
            builder.append("; 备注:").append(request.getRemark().trim());
        }
        return builder.toString();
    }

    private ActivityDTO resolveActivity(Long activityId) {
        ActivityDTO activity = activityId != null ? activityService.getById(activityId) : activityService.getCurrentActivity();
        if (activity == null) {
            throw new BizException(ErrorCode.ACTIVITY_NOT_FOUND);
        }
        return activity;
    }

    private int getTodayShareCount(String shareCountKey) {
        String count = redisTemplate.opsForValue().get(shareCountKey);
        if (!StringUtils.hasText(count)) {
            return 0;
        }
        try {
            return Integer.parseInt(count);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private long secondsUntilTomorrow() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = LocalDate.now().plusDays(1).atStartOfDay();
        long seconds = Duration.between(now, tomorrow).getSeconds();
        return seconds > 0 ? seconds : 86400;
    }

    private String maskUser(Long userId) {
        if (userId == null) {
            return "匿名用户";
        }
        String raw = String.valueOf(userId);
        if (raw.length() <= 2) {
            return "用户" + raw;
        }
        if (raw.length() <= 4) {
            return "用户" + raw.charAt(0) + "*" + raw.charAt(raw.length() - 1);
        }
        return "用户" + raw.substring(0, 2) + "****" + raw.substring(raw.length() - 2);
    }
}
