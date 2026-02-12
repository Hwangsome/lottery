package com.clinic.lottery.api.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 绑定手机号响应 VO
 */
@Data
public class BindPhoneResponseVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String phone;
    private Integer pointsAwarded;
}
