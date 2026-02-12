package com.clinic.lottery.points.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.clinic.lottery.points.entity.PointsRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 积分记录 Mapper
 */
@Mapper
public interface PointsRecordMapper extends BaseMapper<PointsRecord> {
}
