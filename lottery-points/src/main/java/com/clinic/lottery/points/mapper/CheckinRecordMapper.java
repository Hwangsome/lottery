package com.clinic.lottery.points.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.clinic.lottery.points.entity.CheckinRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 签到记录 Mapper
 */
@Mapper
public interface CheckinRecordMapper extends BaseMapper<CheckinRecord> {
}
