package com.clinic.lottery.notify.service;

import com.clinic.lottery.api.dto.WinningRecordDTO;
import com.clinic.lottery.common.constant.PrizeType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 微信模板消息服务
 */
@Slf4j
@Service
public class TemplateMessageService {

    /**
     * 发送中奖通知模板消息
     */
    public boolean sendWinningMessage(WinningRecordDTO record) {
        // 简化实现，实际需要调用微信 API
        log.debug("发送中奖通知，userId={}, prize={}",
                record.getUserId(), record.getPrizeName());

        // 根据奖品类型设置不同的模板
        String templateId;
        if (record.getPrizeType() == PrizeType.COUPON.getCode()) {
            templateId = "coupon_winning_template";
        } else if (record.getPrizeType() == PrizeType.PHYSICAL.getCode()) {
            templateId = "physical_winning_template";
        } else if (record.getPrizeType() == PrizeType.REDPACK.getCode()) {
            templateId = "redpack_winning_template";
        } else {
            // 谢谢参与不发送通知
            return false;
        }

        // 模拟调用微信 API
        try {
            // WechatTemplateMessage template = new WechatTemplateMessage();
            // template.setTouser();
            // template.setTemplateId(templateId);
            // template.setPage("/pages/prize/detail?id=" + record.getId());
            // template.setData(buildTemplateData(record));
            // wechatService.sendTemplateMessage(template);
            log.info("发送中奖通知成功，recordId={}", record.getId());
            return true;
        } catch (Exception e) {
            log.error("发送中奖通知失败，recordId={}", record.getId(), e);
            return false;
        }
    }

    /**
     * 发送签到提醒模板消息
     */
    public boolean sendCheckinReminder(Long userId) {
        log.debug("发送签到提醒，userId={}", userId);
        // 简化实现
        return true;
    }

    /**
     * 发送积分即将过期提醒
     */
    public boolean sendPointsExpireReminder(Long userId, int expiringPoints) {
        log.debug("发送积分过期提醒，userId={}, points={}", userId, expiringPoints);
        // 简化实现
        return true;
    }
}
