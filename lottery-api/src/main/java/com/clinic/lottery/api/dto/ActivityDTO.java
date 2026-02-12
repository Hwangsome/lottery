package com.clinic.lottery.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 活动 DTO
 */
@Data
public class ActivityDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String description;
    private String bannerUrl;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer dailyFreeChances;
    private Integer newUserChances;
    private Integer shareChances;
    private Integer dailyShareLimit;
    private Integer guaranteeCount;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
