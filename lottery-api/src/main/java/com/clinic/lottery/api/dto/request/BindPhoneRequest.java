package com.clinic.lottery.api.dto.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 绑定手机号请求
 */
@Data
public class BindPhoneRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String phone;
}
