package com.clinic.lottery.prize.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.clinic.lottery.prize.entity.Prize;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 奖品 Mapper
 */
@Mapper
public interface PrizeMapper extends BaseMapper<Prize> {

    /**
     * 扣减库存（乐观锁）
     */
    @Update("UPDATE prizes SET remaining_stock = remaining_stock - 1 WHERE id = #{prizeId} AND remaining_stock > 0")
    int deductStock(@Param("prizeId") Long prizeId);

    /**
     * 增加今日已发放数量
     */
    @Update("UPDATE prizes SET daily_sent = daily_sent + 1 WHERE id = #{prizeId}")
    int incrementDailySent(@Param("prizeId") Long prizeId);

    /**
     * 重置今日已发放数量
     */
    @Update("UPDATE prizes SET daily_sent = 0 WHERE activity_id = #{activityId}")
    int resetDailySent(@Param("activityId") Long activityId);
}
