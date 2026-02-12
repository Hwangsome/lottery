package com.clinic.lottery.api.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 中奖记录 VO
 */
@Data
public class WinningRecordVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String prizeName;
    private Integer prizeType;
    private BigDecimal prizeValue;
    private String prizeImageUrl;
    private Integer status;
    private String statusText;
    private String couponCode;
    private String qrcodeUrl;
    private String shippingInfo;
    private LocalDateTime expireTime;
    private LocalDateTime receiveTime;
    private LocalDateTime createdAt;
}
