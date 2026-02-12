package com.clinic.lottery.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.clinic.lottery.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 用户 Mapper
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 增加抽奖次数
     */
    @Update("UPDATE users SET lottery_chances = lottery_chances + #{chances} WHERE id = #{userId}")
    int addChances(@Param("userId") Long userId, @Param("chances") int chances);

    /**
     * 扣减抽奖次数
     */
    @Update("UPDATE users SET lottery_chances = lottery_chances - #{chances} WHERE id = #{userId} AND lottery_chances >= #{chances}")
    int deductChances(@Param("userId") Long userId, @Param("chances") int chances);

    /**
     * 增加积分
     */
    @Update("UPDATE users SET points = points + #{points}, total_points = total_points + #{points} WHERE id = #{userId}")
    int addPoints(@Param("userId") Long userId, @Param("points") int points);

    /**
     * 扣减积分
     */
    @Update("UPDATE users SET points = points - #{points} WHERE id = #{userId} AND points >= #{points}")
    int deductPoints(@Param("userId") Long userId, @Param("points") int points);

    /**
     * 增加连续未中奖次数
     */
    @Update("UPDATE users SET consecutive_lose = consecutive_lose + 1 WHERE id = #{userId}")
    int incrementConsecutiveLose(@Param("userId") Long userId);

    /**
     * 重置连续未中奖次数
     */
    @Update("UPDATE users SET consecutive_lose = 0 WHERE id = #{userId}")
    int resetConsecutiveLose(@Param("userId") Long userId);

    /**
     * 增加中奖次数
     */
    @Update("UPDATE users SET win_count = win_count + 1 WHERE id = #{userId}")
    int incrementWinCount(@Param("userId") Long userId);

    /**
     * 增加抽奖总次数
     */
    @Update("UPDATE users SET total_lottery_count = total_lottery_count + 1 WHERE id = #{userId}")
    int incrementTotalLotteryCount(@Param("userId") Long userId);
}
