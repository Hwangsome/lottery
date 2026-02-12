package com.clinic.lottery.notify.consumer;

import com.clinic.lottery.api.dto.WinningRecordDTO;
import com.clinic.lottery.notify.service.TemplateMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 中奖通知消费者
 */
@Slf4j
@Component
public class WinningNotifyConsumer {

    private final TemplateMessageService templateMessageService;

    public WinningNotifyConsumer(TemplateMessageService templateMessageService) {
        this.templateMessageService = templateMessageService;
    }

    @KafkaListener(topics = "lottery-winning", groupId = "lottery-notify-group")
    public void handleWinningRecord(WinningRecordDTO record) {
        log.info("收到中奖通知，recordId={}, userId={}, prize={}",
                record.getId(), record.getUserId(), record.getPrizeName());

        // 发送微信模板消息通知用户中奖
        try {
            boolean success = templateMessageService.sendWinningMessage(record);
            if (success) {
                log.debug("发送中奖通知成功，recordId={}", record.getId());
            } else {
                log.warn("发送中奖通知失败，recordId={}", record.getId());
            }
        } catch (Exception e) {
            log.error("处理中奖通知失败，recordId={}", record.getId(), e);
        }
    }
}
