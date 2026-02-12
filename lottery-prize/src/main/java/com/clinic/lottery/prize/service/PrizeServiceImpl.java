package com.clinic.lottery.prize.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clinic.lottery.api.dto.ActivityDTO;
import com.clinic.lottery.api.dto.PrizeDTO;
import com.clinic.lottery.api.dto.WinningRecordDTO;
import com.clinic.lottery.api.service.ActivityService;
import com.clinic.lottery.api.service.PrizeService;
import com.clinic.lottery.common.constant.ErrorCode;
import com.clinic.lottery.common.constant.PrizeType;
import com.clinic.lottery.common.constant.RecordStatus;
import com.clinic.lottery.common.exception.BizException;
import com.clinic.lottery.prize.entity.Prize;
import com.clinic.lottery.prize.entity.WinningRecord;
import com.clinic.lottery.prize.mapper.PrizeMapper;
import com.clinic.lottery.prize.mapper.WinningRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 奖品服务 Dubbo 实现
 */
@Slf4j
@Service
@DubboService
public class PrizeServiceImpl implements PrizeService {

    @DubboReference(check = false, protocol = "dubbo")
    private ActivityService activityService;

    private final PrizeMapper prizeMapper;
    private final WinningRecordMapper winningRecordMapper;

    public PrizeServiceImpl(PrizeMapper prizeMapper, WinningRecordMapper winningRecordMapper) {
        this.prizeMapper = prizeMapper;
        this.winningRecordMapper = winningRecordMapper;
    }

    @Override
    public PrizeDTO getById(Long prizeId) {
        Prize prize = prizeMapper.selectById(prizeId);
        return toDTO(prize);
    }

    @Override
    public List<PrizeDTO> getByActivityId(Long activityId) {
        List<Prize> prizes = prizeMapper.selectList(
                new LambdaQueryWrapper<Prize>()
                        .eq(Prize::getActivityId, activityId)
                        .eq(Prize::getStatus, 1)
                        .orderByAsc(Prize::getSortOrder)
        );
        return prizes.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<PrizeDTO> getCurrentActivityPrizes() {
        ActivityDTO currentActivity = activityService.getCurrentActivity();
        if (currentActivity == null) {
            return List.of();
        }
        return getByActivityId(currentActivity.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deductStock(Long prizeId) {
        return prizeMapper.deductStock(prizeId) > 0;
    }

    @Override
    public int getRemainingStock(Long prizeId) {
        Prize prize = prizeMapper.selectById(prizeId);
        return prize != null ? prize.getRemainingStock() : 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WinningRecordDTO createWinningRecord(Long userId, Long activityId, Long prizeId) {
        Prize prize = prizeMapper.selectById(prizeId);
        if (prize == null) {
            throw new BizException(ErrorCode.PRIZE_STOCK_NOT_ENOUGH);
        }

        WinningRecord record = new WinningRecord();
        record.setUserId(userId);
        record.setActivityId(activityId);
        record.setPrizeId(prizeId);
        record.setPrizeName(prize.getName());
        record.setPrizeType(prize.getType());
        record.setPrizeValue(prize.getValue());
        record.setStatus(RecordStatus.PENDING.getCode());
        record.setExpireTime(LocalDateTime.now().plusDays(30));

        winningRecordMapper.insert(record);
        return toDTO(record);
    }

    @Override
    public List<WinningRecordDTO> getUserRecords(Long userId, Integer status, int page, int pageSize) {
        long safePage = page <= 0 ? 1 : page;
        long safePageSize = pageSize <= 0 ? 10 : Math.min(pageSize, 100);

        LambdaQueryWrapper<WinningRecord> query = new LambdaQueryWrapper<WinningRecord>()
                .eq(WinningRecord::getUserId, userId)
                .orderByDesc(WinningRecord::getCreatedAt);

        if (status != null) {
            query.eq(WinningRecord::getStatus, status);
        }

        Page<WinningRecord> pageData = winningRecordMapper.selectPage(new Page<>(safePage, safePageSize), query);
        return pageData.getRecords().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public WinningRecordDTO getRecordById(Long recordId) {
        WinningRecord record = winningRecordMapper.selectById(recordId);
        return toDTO(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WinningRecordDTO receivePhysicalPrize(Long userId, Long recordId, String shippingInfo) {
        if (!StringUtils.hasText(shippingInfo)) {
            throw new BizException(ErrorCode.SHIPPING_INFO_REQUIRED);
        }

        WinningRecord record = winningRecordMapper.selectById(recordId);
        if (record == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "中奖记录不存在");
        }

        if (!record.getUserId().equals(userId)) {
            throw new BizException(ErrorCode.FORBIDDEN, "中奖记录不属于当前用户");
        }

        if (record.getPrizeType() == null || record.getPrizeType() != PrizeType.PHYSICAL.getCode()) {
            throw new BizException(ErrorCode.PARAM_ERROR, "仅实物奖品支持填写收货信息");
        }

        if (record.getStatus() != null && record.getStatus() != RecordStatus.PENDING.getCode()) {
            throw new BizException(ErrorCode.PRIZE_ALREADY_RECEIVED);
        }

        record.setShippingInfo(shippingInfo.trim());
        record.setStatus(RecordStatus.RECEIVED.getCode());
        record.setReceiveTime(LocalDateTime.now());
        winningRecordMapper.updateById(record);

        log.info("实物奖品领取成功，recordId={}, userId={}", recordId, userId);
        return toDTO(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRecordStatus(Long recordId, Integer status) {
        WinningRecord record = new WinningRecord();
        record.setId(recordId);
        record.setStatus(status);
        return winningRecordMapper.updateById(record) > 0;
    }

    @Override
    public List<WinningRecordDTO> getLatestWinningRecords(int limit) {
        int safeLimit = limit <= 0 ? 10 : Math.min(limit, 50);
        List<WinningRecord> records = winningRecordMapper.selectList(
                new LambdaQueryWrapper<WinningRecord>()
                        .ne(WinningRecord::getPrizeType, PrizeType.THANKS.getCode())
                        .orderByDesc(WinningRecord::getCreatedAt)
                        .last("LIMIT " + safeLimit)
        );
        return records.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WinningRecordDTO verifyCoupon(String couponCode, String verifyUser) {
        WinningRecord record = winningRecordMapper.selectOne(
                new LambdaQueryWrapper<WinningRecord>()
                        .eq(WinningRecord::getCouponCode, couponCode)
        );

        if (record == null) {
            throw new BizException(ErrorCode.COUPON_NOT_FOUND);
        }

        if (record.getStatus() == RecordStatus.USED.getCode()) {
            throw new BizException(ErrorCode.COUPON_USED);
        }

        if (LocalDateTime.now().isAfter(record.getExpireTime())) {
            throw new BizException(ErrorCode.COUPON_EXPIRED);
        }

        record.setStatus(RecordStatus.USED.getCode());
        record.setVerifyTime(LocalDateTime.now());
        record.setVerifyUser(verifyUser);
        winningRecordMapper.updateById(record);

        log.info("优惠券核销成功，couponCode={}, userId={}", couponCode, record.getUserId());
        return toDTO(record);
    }

    private PrizeDTO toDTO(Prize prize) {
        if (prize == null) {
            return null;
        }
        PrizeDTO dto = new PrizeDTO();
        BeanUtils.copyProperties(prize, dto);
        return dto;
    }

    private WinningRecordDTO toDTO(WinningRecord record) {
        if (record == null) {
            return null;
        }
        WinningRecordDTO dto = new WinningRecordDTO();
        BeanUtils.copyProperties(record, dto);
        if (record.getStatus() != null) {
            dto.setStatusText(RecordStatus.fromCode(record.getStatus()).getDesc());
        }
        if (record.getPrizeId() != null) {
            Prize prize = prizeMapper.selectById(record.getPrizeId());
            if (prize != null) {
                dto.setPrizeImageUrl(prize.getImageUrl());
            }
        }
        return dto;
    }
}
