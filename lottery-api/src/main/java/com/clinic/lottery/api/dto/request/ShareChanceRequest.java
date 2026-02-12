package com.clinic.lottery.api.dto.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 分享增加抽奖次数请求
 */
@Data
public class ShareChanceRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long activityId;
    private Long friendUserId;
}
