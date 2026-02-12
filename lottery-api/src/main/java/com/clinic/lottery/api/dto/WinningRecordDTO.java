package com.clinic.lottery.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 中奖记录 DTO
 */
@Data
public class WinningRecordDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private Long activityId;
    private Long prizeId;
    private String prizeName;
    private Integer prizeType;
    private BigDecimal prizeValue;
    private String prizeImageUrl;
    private Integer status;
    private String statusText;
    private String couponCode;
    private String qrcodeUrl;
    private LocalDateTime expireTime;
    private LocalDateTime receiveTime;
    private LocalDateTime verifyTime;
    private String verifyUser;
    private String shippingInfo;
    private String redpackInfo;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
