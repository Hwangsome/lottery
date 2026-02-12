package com.clinic.lottery.user.controller;

import com.clinic.lottery.api.dto.UserDTO;
import com.clinic.lottery.api.dto.request.BindPhoneRequest;
import com.clinic.lottery.api.dto.request.UpdateUserInfoRequest;
import com.clinic.lottery.api.service.UserService;
import com.clinic.lottery.api.vo.BindPhoneResponseVO;
import com.clinic.lottery.api.vo.UserInfoVO;
import com.clinic.lottery.common.constant.ErrorCode;
import com.clinic.lottery.common.exception.BizException;
import com.clinic.lottery.common.result.Result;
import com.clinic.lottery.common.util.CommonUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.StringUtils;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "用户管理", description = "用户信息相关接口")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/info")
    @Operation(summary = "获取用户信息", description = "获取当前登录用户的详细信息")
    public Result<UserInfoVO> getUserInfo(@RequestHeader("X-User-Id") Long userId) {
        log.info("获取用户信息，userId={}", userId);

        UserDTO user = userService.getUserInfo(userId);
        if (user == null) {
            throw new BizException(ErrorCode.USER_NOT_FOUND);
        }

        UserInfoVO vo = new UserInfoVO();
        vo.setId(String.valueOf(user.getId()));
        vo.setNickname(user.getNickname());
        vo.setAvatarUrl(user.getAvatarUrl());
        vo.setPhone(CommonUtil.maskPhone(user.getPhone()));
        vo.setPoints(user.getPoints());
        vo.setLotteryChances(user.getLotteryChances());
        vo.setTotalLotteryCount(user.getTotalLotteryCount());
        vo.setWinCount(user.getWinCount());

        return Result.success(vo);
    }

    @PutMapping("/info")
    @Operation(summary = "更新用户信息", description = "更新当前登录用户的昵称、头像、手机号")
    public Result<UserInfoVO> updateUserInfo(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody UpdateUserInfoRequest request) {
        log.info("更新用户信息，userId={}", userId);

        if (request == null || (!StringUtils.hasText(request.getNickname())
                && !StringUtils.hasText(request.getAvatarUrl())
                && !StringUtils.hasText(request.getPhone()))) {
            throw new BizException(ErrorCode.PARAM_ERROR, "至少需要更新一个字段");
        }

        boolean updated = userService.updateUserInfo(
                userId,
                request.getNickname(),
                request.getAvatarUrl(),
                request.getPhone()
        );
        if (!updated) {
            throw new BizException(ErrorCode.USER_NOT_FOUND);
        }

        UserDTO user = userService.getUserInfo(userId);
        if (user == null) {
            throw new BizException(ErrorCode.USER_NOT_FOUND);
        }

        UserInfoVO vo = new UserInfoVO();
        vo.setId(String.valueOf(user.getId()));
        vo.setNickname(user.getNickname());
        vo.setAvatarUrl(user.getAvatarUrl());
        vo.setPhone(CommonUtil.maskPhone(user.getPhone()));
        vo.setPoints(user.getPoints());
        vo.setLotteryChances(user.getLotteryChances());
        vo.setTotalLotteryCount(user.getTotalLotteryCount());
        vo.setWinCount(user.getWinCount());
        return Result.success(vo);
    }

    @PostMapping("/bindPhone")
    @Operation(summary = "绑定手机号", description = "绑定手机号，首次绑定送50积分")
    public Result<BindPhoneResponseVO> bindPhone(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody BindPhoneRequest request) {
        log.info("绑定手机号，userId={}", userId);

        // 这里简化处理，实际需要解密微信加密数据获取手机号
        String phone = request.getPhone();
        if (phone == null || phone.isEmpty()) {
            throw new BizException(ErrorCode.PARAM_ERROR, "手机号不能为空");
        }

        UserDTO user = userService.getById(userId);
        if (user == null) {
            throw new BizException(ErrorCode.USER_NOT_FOUND);
        }

        // 检查是否已绑定
        int pointsAwarded = 0;
        if (user.getPhone() == null || user.getPhone().isEmpty()) {
            // 首次绑定送50积分
            user.setPhone(phone);
            userService.updateUser(user);
            userService.addPoints(userId, 50, "绑定手机号");
            pointsAwarded = 50;
            log.info("用户首次绑定手机号，送50积分，userId={}", userId);
        } else {
            throw new BizException(ErrorCode.PHONE_BINDIED);
        }

        BindPhoneResponseVO vo = new BindPhoneResponseVO();
        vo.setPhone(CommonUtil.maskPhone(phone));
        vo.setPointsAwarded(pointsAwarded);

        return Result.success(vo);
    }
}
