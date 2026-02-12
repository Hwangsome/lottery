package com.clinic.lottery.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 积分记录 DTO
 */
@Data
public class PointsRecordDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private Integer type;
    private String typeText;
    private Integer changeAmount;
    private Integer balance;
    private String sourceId;
    private LocalDateTime expireTime;
    private String remark;
    private LocalDateTime createdAt;
}
