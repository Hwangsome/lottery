package com.clinic.lottery.api.service;

import com.clinic.lottery.api.dto.UserDTO;

/**
 * 用户服务 Dubbo 接口
 */
public interface UserService {

    /**
     * 根据 openid 查询用户
     */
    UserDTO getByOpenid(String openid);

    /**
     * 根据 ID 查询用户
     */
    UserDTO getById(Long userId);

    /**
     * 获取用户详细信息
     */
    UserDTO getUserInfo(Long userId);

    /**
     * 创建用户
     */
    UserDTO createUser(UserDTO userDTO);

    /**
     * 更新用户信息
     */
    boolean updateUser(UserDTO userDTO);

    /**
     * 更新用户基础信息（昵称、头像、手机号）
     */
    boolean updateUserInfo(Long userId, String nickname, String avatarUrl, String phone);

    /**
     * 增加抽奖次数
     */
    boolean addChances(Long userId, int chances, String reason);

    /**
     * 扣减抽奖次数
     */
    boolean deductChances(Long userId, int chances);

    /**
     * 获取用户抽奖次数
     */
    int getChances(Long userId);

    /**
     * 增加积分
     */
    boolean addPoints(Long userId, int points, String reason);

    /**
     * 扣减积分
     */
    boolean deductPoints(Long userId, int points);

    /**
     * 获取用户积分
     */
    int getPoints(Long userId);

    /**
     * 增加连续未中奖次数
     */
    boolean incrementConsecutiveLose(Long userId);

    /**
     * 重置连续未中奖次数
     */
    boolean resetConsecutiveLose(Long userId);

    /**
     * 获取连续未中奖次数
     */
    int getConsecutiveLose(Long userId);

    /**
     * 增加中奖次数
     */
    boolean incrementWinCount(Long userId);

    /**
     * 增加抽奖总次数
     */
    boolean incrementTotalLotteryCount(Long userId);
}
