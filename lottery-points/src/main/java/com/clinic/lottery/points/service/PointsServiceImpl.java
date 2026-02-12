package com.clinic.lottery.points.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.clinic.lottery.api.dto.PointsRecordDTO;
import com.clinic.lottery.api.service.PointsService;
import com.clinic.lottery.common.constant.ErrorCode;
import com.clinic.lottery.common.constant.PointsType;
import com.clinic.lottery.common.exception.BizException;
import com.clinic.lottery.points.config.PointsCheckinProperties;
import com.clinic.lottery.points.entity.CheckinRecord;
import com.clinic.lottery.points.entity.PointsRecord;
import com.clinic.lottery.points.mapper.CheckinRecordMapper;
import com.clinic.lottery.points.mapper.PointsRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 积分服务 Dubbo 实现
 */
@Slf4j
@Service
@DubboService
public class PointsServiceImpl implements PointsService {

    private final PointsRecordMapper pointsRecordMapper;
    private final CheckinRecordMapper checkinRecordMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final PointsCheckinProperties checkinProperties;

    public PointsServiceImpl(PointsRecordMapper pointsRecordMapper,
                             CheckinRecordMapper checkinRecordMapper,
                             StringRedisTemplate stringRedisTemplate,
                             PointsCheckinProperties checkinProperties) {
        this.pointsRecordMapper = pointsRecordMapper;
        this.checkinRecordMapper = checkinRecordMapper;
        this.stringRedisTemplate = stringRedisTemplate;
        this.checkinProperties = checkinProperties;
    }

    @Override
    public int getPoints(Long userId) {
        // 获取用户最新积分余额（最后一条记录的余额）
        List<PointsRecord> records = pointsRecordMapper.selectList(
                new LambdaQueryWrapper<PointsRecord>()
                        .eq(PointsRecord::getUserId, userId)
                        .orderByDesc(PointsRecord::getCreatedAt)
                        .last("LIMIT 1")
        );
        return records.isEmpty() ? 0 : records.get(0).getBalance();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addPoints(Long userId, int points, int type, String sourceId, String remark) {
        int currentBalance = getPoints(userId);
        PointsRecord record = new PointsRecord();
        record.setUserId(userId);
        record.setType(type);
        record.setChangeAmount(points);
        record.setBalance(currentBalance + points);
        record.setSourceId(sourceId);
        record.setRemark(remark);
        record.setExpireTime(LocalDateTime.now().plusYears(1)); // 积分有效期 1 年
        record.setCreatedAt(LocalDateTime.now());
        pointsRecordMapper.insert(record);
        log.debug("添加积分成功，userId={}, points={}, type={}, remark={}",
                userId, points, type, remark);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deductPoints(Long userId, int points, int type, String sourceId, String remark) {
        int currentBalance = getPoints(userId);
        if (currentBalance < points) {
            throw new BizException(ErrorCode.POINTS_NOT_ENOUGH);
        }

        PointsRecord record = new PointsRecord();
        record.setUserId(userId);
        record.setType(type);
        record.setChangeAmount(-points);
        record.setBalance(currentBalance - points);
        record.setSourceId(sourceId);
        record.setRemark(remark);
        record.setCreatedAt(LocalDateTime.now());
        pointsRecordMapper.insert(record);
        log.debug("扣减积分成功，userId={}, points={}, type={}, remark={}",
                userId, points, type, remark);
        return true;
    }

    @Override
    public List<PointsRecordDTO> getRecords(Long userId, Integer type, int page, int pageSize) {
        LambdaQueryWrapper<PointsRecord> query = new LambdaQueryWrapper<PointsRecord>()
                .eq(PointsRecord::getUserId, userId)
                .orderByDesc(PointsRecord::getCreatedAt);

        if (type != null) {
            query.eq(PointsRecord::getType, type);
        }

        List<PointsRecord> records = pointsRecordMapper.selectList(query);
        return records.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PointsRecordDTO checkin(Long userId) {
        LocalDate today = LocalDate.now();
        String doneKey = buildCheckinDoneKey(userId, today);
        String lockKey = buildCheckinLockKey(userId, today);

        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(doneKey))) {
            throw new BizException(ErrorCode.ALREADY_CHECKED_IN);
        }

        boolean locked = Boolean.TRUE.equals(
                stringRedisTemplate.opsForValue().setIfAbsent(lockKey, "1", Duration.ofSeconds(5))
        );
        if (!locked) {
            throw new BizException(ErrorCode.ALREADY_CHECKED_IN);
        }

        boolean hasCheckedIn = checkinRecordMapper.selectCount(
                new LambdaQueryWrapper<CheckinRecord>()
                        .eq(CheckinRecord::getUserId, userId)
                        .eq(CheckinRecord::getCheckinDate, today)
                        .last("LIMIT 1")
        ) > 0;
        if (hasCheckedIn) {
            throw new BizException(ErrorCode.ALREADY_CHECKED_IN);
        }

        CheckinRecord lastRecord = checkinRecordMapper.selectOne(
                new LambdaQueryWrapper<CheckinRecord>()
                        .eq(CheckinRecord::getUserId, userId)
                        .orderByDesc(CheckinRecord::getCheckinDate)
                        .last("LIMIT 1")
        );
        int consecutiveDays = 1;
        if (lastRecord != null && lastRecord.getCheckinDate() != null
                && lastRecord.getCheckinDate().equals(today.minusDays(1))) {
            consecutiveDays = lastRecord.getConsecutiveDays() == null ? 1 : lastRecord.getConsecutiveDays() + 1;
        }

        int bonusPoints = getBonusPoints(consecutiveDays);
        int bonusChances = getBonusChances(consecutiveDays);
        int pointsAwarded = checkinProperties.getDailyPoints() + bonusPoints;
        int chancesAwarded = checkinProperties.getDailyChances() + bonusChances;

        CheckinRecord checkinRecord = new CheckinRecord();
        checkinRecord.setUserId(userId);
        checkinRecord.setCheckinDate(today);
        checkinRecord.setConsecutiveDays(consecutiveDays);
        checkinRecord.setPointsAwarded(pointsAwarded);
        checkinRecord.setChancesAwarded(chancesAwarded);
        checkinRecord.setCreatedAt(LocalDateTime.now());
        try {
            checkinRecordMapper.insert(checkinRecord);
        } catch (DuplicateKeyException e) {
            throw new BizException(ErrorCode.ALREADY_CHECKED_IN);
        }

        PointsRecord record = new PointsRecord();
        record.setUserId(userId);
        record.setType(PointsType.CHECKIN.getCode());
        record.setChangeAmount(pointsAwarded);
        record.setBalance(getPoints(userId) + pointsAwarded);
        record.setRemark("每日签到");
        record.setExpireTime(LocalDateTime.now().plusYears(1));
        record.setCreatedAt(LocalDateTime.now());
        pointsRecordMapper.insert(record);

        stringRedisTemplate.opsForValue().set(doneKey, "1", secondsUntilTomorrow());
        log.debug("签到成功，userId={}", userId);
        return toDTO(record);
    }

    @Override
    public boolean hasCheckedInToday(Long userId) {
        LocalDate today = LocalDate.now();
        String doneKey = buildCheckinDoneKey(userId, today);
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(doneKey))) {
            return true;
        }

        boolean hasCheckedIn = checkinRecordMapper.selectCount(
                new LambdaQueryWrapper<CheckinRecord>()
                        .eq(CheckinRecord::getUserId, userId)
                        .eq(CheckinRecord::getCheckinDate, today)
                        .last("LIMIT 1")
        ) > 0;
        if (hasCheckedIn) {
            stringRedisTemplate.opsForValue().set(doneKey, "1", secondsUntilTomorrow());
        }
        return hasCheckedIn;
    }

    @Override
    public int getConsecutiveDays(Long userId) {
        CheckinRecord lastRecord = checkinRecordMapper.selectOne(
                new LambdaQueryWrapper<CheckinRecord>()
                        .eq(CheckinRecord::getUserId, userId)
                        .orderByDesc(CheckinRecord::getCheckinDate)
                        .last("LIMIT 1")
        );
        return lastRecord == null || lastRecord.getConsecutiveDays() == null
                ? 0
                : lastRecord.getConsecutiveDays();
    }

    private int getBonusPoints(int consecutiveDays) {
        int bonus = 0;
        for (PointsCheckinProperties.BonusRule rule : checkinProperties.getConsecutiveBonus()) {
            if (consecutiveDays >= rule.getDays()) {
                bonus = Math.max(bonus, rule.getBonusPoints());
            }
        }
        return bonus;
    }

    private int getBonusChances(int consecutiveDays) {
        int bonus = 0;
        for (PointsCheckinProperties.BonusRule rule : checkinProperties.getConsecutiveBonus()) {
            if (consecutiveDays >= rule.getDays()) {
                bonus = Math.max(bonus, rule.getBonusChances());
            }
        }
        return bonus;
    }

    private Duration secondsUntilTomorrow() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrowStart = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIDNIGHT);
        return Duration.between(now, tomorrowStart);
    }

    private String buildCheckinDoneKey(Long userId, LocalDate date) {
        return "lottery:points:checkin:done:" + userId + ":" + date;
    }

    private String buildCheckinLockKey(Long userId, LocalDate date) {
        return "lottery:points:checkin:lock:" + userId + ":" + date;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean exchangeForChances(Long userId, int times) {
        int pointsNeeded = times * 100; // 100积分换1次机会
        return deductPoints(userId, pointsNeeded, PointsType.EXCHANGE_LOTTERY.getCode(),
                null, "积分兑换抽奖机会");
    }

    @Override
    public int getExpiringSoonPoints(Long userId, int days) {
        // 简化处理，返回 0
        return 0;
    }

    private PointsRecordDTO toDTO(PointsRecord record) {
        if (record == null) {
            return null;
        }
        PointsRecordDTO dto = new PointsRecordDTO();
        BeanUtils.copyProperties(record, dto);
        dto.setTypeText(PointsType.fromCode(record.getType()).getDesc());
        return dto;
    }
}
