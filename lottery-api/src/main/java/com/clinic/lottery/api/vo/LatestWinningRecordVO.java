package com.clinic.lottery.api.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 最新中奖滚动记录 VO
 */
@Data
public class LatestWinningRecordVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userDisplayName;
    private String prizeName;
    private LocalDateTime winTime;
}
