package com.clinic.lottery.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.clinic.lottery.activity.entity.Activity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 活动 Mapper
 */
@Mapper
public interface ActivityMapper extends BaseMapper<Activity> {
}
