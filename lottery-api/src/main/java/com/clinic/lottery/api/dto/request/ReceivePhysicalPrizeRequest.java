package com.clinic.lottery.api.dto.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 领取实物奖品请求
 */
@Data
public class ReceivePhysicalPrizeRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long recordId;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String remark;
}
