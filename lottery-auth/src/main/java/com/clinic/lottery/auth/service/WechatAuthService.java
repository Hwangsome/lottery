package com.clinic.lottery.auth.service;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.clinic.lottery.api.dto.UserDTO;
import com.clinic.lottery.api.service.UserService;
import com.clinic.lottery.auth.dto.LoginResponse;
import com.clinic.lottery.auth.dto.UserInfoVO;
import com.clinic.lottery.auth.dto.WechatLoginRequest;
import com.clinic.lottery.common.constant.ErrorCode;
import com.clinic.lottery.common.exception.BizException;
import com.clinic.lottery.common.util.CommonUtil;
import com.clinic.lottery.common.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 微信认证服务
 */
@Slf4j
@Service
public class WechatAuthService {

    @Value("${wechat.appid}")
    private String appid;

    @Value("${wechat.secret}")
    private String secret;

    @Value("${jwt.expire-seconds:604800}")
    private Long expireSeconds;

    @DubboReference(check = false, protocol = "dubbo")
    private UserService userService;

    private final JwtUtil jwtUtil;

    public WechatAuthService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * 微信小程序登录
     */
    public LoginResponse login(WechatLoginRequest request) {
        // 1. 用 code 换取 openid 和 session_key
        String openid = code2Session(request.getCode());

        // 2. 查询或创建用户
        UserDTO user = userService.getByOpenid(openid);
        boolean isNewUser = false;

        if (user == null) {
            // 新用户注册
            user = new UserDTO();
            user.setOpenid(openid);
            user.setNickname("微信用户");
            user.setLotteryChances(3);  // 新用户送 3 次抽奖机会
            user.setPoints(0);
            user.setTotalPoints(0);
            user.setTotalLotteryCount(0);
            user.setWinCount(0);
            user.setConsecutiveLose(0);
            user.setStatus(1);

            user = userService.createUser(user);
            isNewUser = true;
            log.info("新用户注册成功，userId={}, openid={}", user.getId(), openid);
        }

        // 3. 生成 JWT Token
        String token = jwtUtil.generateToken(user.getId(), openid);

        // 4. 构建响应
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setExpiresIn(expireSeconds);
        response.setUserInfo(buildUserInfo(user));
        response.setIsNewUser(isNewUser);

        return response;
    }

    /**
     * 用 code 换取 openid
     */
    private String code2Session(String code) {
        String url = String.format(
                "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                appid, secret, code
        );

        try {
            String response = HttpUtil.get(url, 5000);
            log.debug("微信 code2session 响应: {}", response);

            JSONObject json = JSON.parseObject(response);
            if (json.containsKey("errcode") && json.getIntValue("errcode") != 0) {
                log.error("微信登录失败: {}", response);
                throw new BizException(ErrorCode.WECHAT_LOGIN_FAILED, json.getString("errmsg"));
            }

            String openid = json.getString("openid");
            if (openid == null || openid.isEmpty()) {
                throw new BizException(ErrorCode.WECHAT_LOGIN_FAILED, "获取 openid 失败");
            }

            return openid;
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            log.error("微信登录异常", e);
            throw new BizException(ErrorCode.WECHAT_LOGIN_FAILED, "微信登录失败");
        }
    }

    /**
     * 构建用户信息 VO
     */
    private UserInfoVO buildUserInfo(UserDTO user) {
        UserInfoVO vo = new UserInfoVO();
        vo.setId(String.valueOf(user.getId()));
        vo.setNickname(user.getNickname());
        vo.setAvatarUrl(user.getAvatarUrl());
        vo.setPhone(CommonUtil.maskPhone(user.getPhone()));
        vo.setPoints(user.getPoints());
        vo.setLotteryChances(user.getLotteryChances());
        vo.setTotalLotteryCount(user.getTotalLotteryCount());
        vo.setWinCount(user.getWinCount());
        return vo;
    }
}
