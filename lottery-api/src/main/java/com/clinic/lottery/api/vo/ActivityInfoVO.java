package com.clinic.lottery.api.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 活动基础信息 VO（供前端展示）
 */
@Data
public class ActivityInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String description;
    private String bannerUrl;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer status;
}
