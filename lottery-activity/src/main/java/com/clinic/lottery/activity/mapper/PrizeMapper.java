package com.clinic.lottery.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.clinic.lottery.activity.entity.Prize;
import org.apache.ibatis.annotations.Mapper;

/**
 * 奖品 Mapper
 */
@Mapper
public interface PrizeMapper extends BaseMapper<Prize> {
}
