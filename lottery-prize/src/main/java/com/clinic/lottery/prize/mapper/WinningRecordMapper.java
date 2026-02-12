package com.clinic.lottery.prize.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.clinic.lottery.prize.entity.WinningRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 中奖记录 Mapper
 */
@Mapper
public interface WinningRecordMapper extends BaseMapper<WinningRecord> {
}
