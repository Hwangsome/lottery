package com.clinic.lottery.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.clinic.lottery.api.dto.UserDTO;
import com.clinic.lottery.api.service.UserService;
import com.clinic.lottery.user.entity.User;
import com.clinic.lottery.user.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 用户服务 Dubbo 实现
 */
@Slf4j
@Service
@DubboService
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserDTO getByOpenid(String openid) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getOpenid, openid)
        );
        return toDTO(user);
    }

    @Override
    public UserDTO getById(Long userId) {
        User user = userMapper.selectById(userId);
        return toDTO(user);
    }

    @Override
    public UserDTO getUserInfo(Long userId) {
        return getById(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDTO createUser(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);

        // 避免插入时 created_at/updated_at 为空触发数据库约束异常
        LocalDateTime now = LocalDateTime.now();
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(now);
        }
        if (user.getUpdatedAt() == null) {
            user.setUpdatedAt(now);
        }

        userMapper.insert(user);
        log.info("创建用户成功，userId={}", user.getId());
        return toDTO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUser(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        int rows = userMapper.updateById(user);
        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserInfo(Long userId, String nickname, String avatarUrl, String phone) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return false;
        }

        boolean changed = false;
        if (StringUtils.hasText(nickname)) {
            user.setNickname(nickname.trim());
            changed = true;
        }
        if (StringUtils.hasText(avatarUrl)) {
            user.setAvatarUrl(avatarUrl.trim());
            changed = true;
        }
        if (StringUtils.hasText(phone)) {
            user.setPhone(phone.trim());
            changed = true;
        }

        if (!changed) {
            return true;
        }
        return userMapper.updateById(user) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addChances(Long userId, int chances, String reason) {
        int rows = userMapper.addChances(userId, chances);
        if (rows > 0) {
            log.info("增加抽奖次数成功，userId={}, chances={}, reason={}", userId, chances, reason);
        }
        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deductChances(Long userId, int chances) {
        int rows = userMapper.deductChances(userId, chances);
        if (rows > 0) {
            log.info("扣减抽奖次数成功，userId={}, chances={}", userId, chances);
        }
        return rows > 0;
    }

    @Override
    public int getChances(Long userId) {
        User user = userMapper.selectById(userId);
        return user != null ? user.getLotteryChances() : 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addPoints(Long userId, int points, String reason) {
        int rows = userMapper.addPoints(userId, points);
        if (rows > 0) {
            log.info("增加积分成功，userId={}, points={}, reason={}", userId, points, reason);
        }
        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deductPoints(Long userId, int points) {
        int rows = userMapper.deductPoints(userId, points);
        if (rows > 0) {
            log.info("扣减积分成功，userId={}, points={}", userId, points);
        }
        return rows > 0;
    }

    @Override
    public int getPoints(Long userId) {
        User user = userMapper.selectById(userId);
        return user != null ? user.getPoints() : 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean incrementConsecutiveLose(Long userId) {
        return userMapper.incrementConsecutiveLose(userId) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean resetConsecutiveLose(Long userId) {
        return userMapper.resetConsecutiveLose(userId) > 0;
    }

    @Override
    public int getConsecutiveLose(Long userId) {
        User user = userMapper.selectById(userId);
        return user != null ? user.getConsecutiveLose() : 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean incrementWinCount(Long userId) {
        return userMapper.incrementWinCount(userId) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean incrementTotalLotteryCount(Long userId) {
        return userMapper.incrementTotalLotteryCount(userId) > 0;
    }

    private UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(user, dto);
        return dto;
    }
}
