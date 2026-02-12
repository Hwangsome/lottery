package com.clinic.lottery.notify.consumer;

import com.clinic.lottery.api.dto.WinningRecordDTO;
import com.clinic.lottery.notify.service.RedpackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 红包发放消费者
 */
@Slf4j
@Component
public class RedpackConsumer {

    private final RedpackService redpackService;

    public RedpackConsumer(RedpackService redpackService) {
        this.redpackService = redpackService;
    }

    @KafkaListener(topics = "lottery-redpack", groupId = "lottery-notify-group")
    public void handleRedpack(WinningRecordDTO record) {
        log.info("收到红包发放通知，recordId={}, userId={}, prize={}",
                record.getId(), record.getUserId(), record.getPrizeName());

        // 发送微信红包
        try {
            boolean success = redpackService.sendRedpack(record);
            if (success) {
                log.debug("发送红包成功，recordId={}", record.getId());
            } else {
                log.warn("发送红包失败，recordId={}", record.getId());
            }
        } catch (Exception e) {
            log.error("处理红包发放失败，recordId={}", record.getId(), e);
        }
    }
}
