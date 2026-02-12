package com.clinic.lottery.api.dto.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 更新用户信息请求
 */
@Data
public class UpdateUserInfoRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nickname;
    private String avatarUrl;
    private String phone;
}
