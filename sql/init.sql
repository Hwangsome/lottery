-- =====================================================
-- 牙科诊所大转盘抽奖小程序 - 数据库初始化脚本
-- =====================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `lottery` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `lottery`;
SET NAMES utf8mb4;

-- =====================================================
-- 1. 用户表
-- =====================================================
CREATE TABLE IF NOT EXISTS `users` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `openid` varchar(64) NOT NULL COMMENT '微信OpenID',
  `unionid` varchar(64) DEFAULT NULL COMMENT '微信UnionID',
  `nickname` varchar(64) DEFAULT '' COMMENT '昵称',
  `avatar_url` varchar(512) DEFAULT '' COMMENT '头像URL',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号(加密存储)',
  `gender` tinyint DEFAULT 0 COMMENT '性别: 0未知 1男 2女',
  `points` int unsigned DEFAULT 0 COMMENT '当前积分',
  `total_points` int unsigned DEFAULT 0 COMMENT '累计获得积分',
  `lottery_chances` int unsigned DEFAULT 0 COMMENT '当前抽奖次数',
  `total_lottery_count` int unsigned DEFAULT 0 COMMENT '累计抽奖次数',
  `win_count` int unsigned DEFAULT 0 COMMENT '中奖次数',
  `consecutive_lose` int unsigned DEFAULT 0 COMMENT '连续未中奖次数(保底用)',
  `device_id` varchar(64) DEFAULT NULL COMMENT '设备标识',
  `status` tinyint DEFAULT 1 COMMENT '状态: 0禁用 1正常',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_openid` (`openid`),
  KEY `idx_unionid` (`unionid`),
  KEY `idx_phone` (`phone`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- =====================================================
-- 2. 活动表
-- =====================================================
CREATE TABLE IF NOT EXISTS `activities` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '活动ID',
  `name` varchar(100) NOT NULL COMMENT '活动名称',
  `description` text COMMENT '活动描述',
  `banner_url` varchar(512) DEFAULT NULL COMMENT '活动banner图',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `daily_free_chances` int DEFAULT 1 COMMENT '每日免费抽奖次数',
  `new_user_chances` int DEFAULT 3 COMMENT '新用户赠送次数',
  `share_chances` int DEFAULT 1 COMMENT '分享获得次数',
  `daily_share_limit` int DEFAULT 3 COMMENT '每日分享获取上限',
  `guarantee_count` int DEFAULT 10 COMMENT '保底次数(N次必中)',
  `status` tinyint DEFAULT 1 COMMENT '状态: 0下线 1上线',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status_time` (`status`, `start_time`, `end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='活动表';

-- =====================================================
-- 3. 奖品表
-- =====================================================
CREATE TABLE IF NOT EXISTS `prizes` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '奖品ID',
  `activity_id` bigint unsigned NOT NULL COMMENT '所属活动ID',
  `name` varchar(100) NOT NULL COMMENT '奖品名称',
  `type` tinyint NOT NULL COMMENT '类型: 1优惠券 2实物 3红包 4谢谢参与',
  `image_url` varchar(512) DEFAULT NULL COMMENT '奖品图片',
  `description` text COMMENT '奖品描述',
  `value` decimal(10,2) DEFAULT 0 COMMENT '奖品价值(元)',
  `probability` decimal(8,4) NOT NULL COMMENT '中奖概率(%)',
  `total_stock` int DEFAULT -1 COMMENT '总库存(-1表示不限)',
  `remaining_stock` int DEFAULT -1 COMMENT '剩余库存',
  `daily_limit` int DEFAULT -1 COMMENT '每日发放上限(-1不限)',
  `daily_sent` int DEFAULT 0 COMMENT '今日已发放数量',
  `is_guarantee` tinyint DEFAULT 0 COMMENT '是否保底奖品: 0否 1是',
  `sort_order` int DEFAULT 0 COMMENT '转盘位置排序',
  `coupon_config` json DEFAULT NULL COMMENT '优惠券配置(JSON)',
  `redpack_config` json DEFAULT NULL COMMENT '红包配置(JSON)',
  `status` tinyint DEFAULT 1 COMMENT '状态: 0禁用 1启用',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_activity_status` (`activity_id`, `status`),
  KEY `idx_sort` (`activity_id`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='奖品表';

-- =====================================================
-- 4. 中奖记录表
-- =====================================================
CREATE TABLE IF NOT EXISTS `winning_records` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` bigint unsigned NOT NULL COMMENT '用户ID',
  `activity_id` bigint unsigned NOT NULL COMMENT '活动ID',
  `prize_id` bigint unsigned NOT NULL COMMENT '奖品ID',
  `prize_name` varchar(100) NOT NULL COMMENT '奖品名称(冗余)',
  `prize_type` tinyint NOT NULL COMMENT '奖品类型',
  `prize_value` decimal(10,2) DEFAULT 0 COMMENT '奖品价值',
  `status` tinyint DEFAULT 0 COMMENT '状态: 0待领取 1已领取 2已发货 3已使用 4已过期 5发放失败',
  `coupon_code` varchar(32) DEFAULT NULL COMMENT '优惠券码',
  `qrcode_url` varchar(512) DEFAULT NULL COMMENT '核销二维码URL',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  `receive_time` datetime DEFAULT NULL COMMENT '领取时间',
  `verify_time` datetime DEFAULT NULL COMMENT '核销时间',
  `verify_user` varchar(64) DEFAULT NULL COMMENT '核销人',
  `shipping_info` json DEFAULT NULL COMMENT '收货信息(实物奖品)',
  `redpack_info` json DEFAULT NULL COMMENT '红包发放信息',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '中奖时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_activity_id` (`activity_id`),
  KEY `idx_prize_id` (`prize_id`),
  KEY `idx_coupon_code` (`coupon_code`),
  KEY `idx_status` (`status`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='中奖记录表';

-- =====================================================
-- 5. 抽奖次数记录表
-- =====================================================
CREATE TABLE IF NOT EXISTS `lottery_chances` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` bigint unsigned NOT NULL COMMENT '用户ID',
  `activity_id` bigint unsigned NOT NULL COMMENT '活动ID',
  `type` tinyint NOT NULL COMMENT '类型: 1新用户赠送 2每日免费 3消费获得 4积分兑换 5分享获得 6签到获得 7抽奖消耗 8管理员调整',
  `change_amount` int NOT NULL COMMENT '变动数量(正数增加,负数减少)',
  `balance` int NOT NULL COMMENT '变动后余额',
  `source_id` varchar(64) DEFAULT NULL COMMENT '来源ID(消费单号/分享记录等)',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_activity` (`user_id`, `activity_id`),
  KEY `idx_type` (`type`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='抽奖次数记录表';

-- =====================================================
-- 6. 积分记录表
-- =====================================================
CREATE TABLE IF NOT EXISTS `points_records` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` bigint unsigned NOT NULL COMMENT '用户ID',
  `type` tinyint NOT NULL COMMENT '类型: 1消费获得 2签到获得 3连续签到奖励 4完善资料 5预约就诊 6评价服务 7邀请用户 8兑换抽奖 9兑换优惠券 10过期扣除 11管理员调整',
  `change_amount` int NOT NULL COMMENT '变动数量',
  `balance` int NOT NULL COMMENT '变动后余额',
  `source_id` varchar(64) DEFAULT NULL COMMENT '来源ID',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_type` (`type`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='积分记录表';

-- =====================================================
-- 7. 消费记录表
-- =====================================================
CREATE TABLE IF NOT EXISTS `consumption_records` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` bigint unsigned NOT NULL COMMENT '用户ID',
  `order_no` varchar(64) NOT NULL COMMENT '订单号',
  `amount` decimal(10,2) NOT NULL COMMENT '消费金额',
  `service_type` varchar(50) DEFAULT NULL COMMENT '服务类型',
  `points_awarded` int DEFAULT 0 COMMENT '获得积分',
  `chances_awarded` int DEFAULT 0 COMMENT '获得抽奖次数',
  `operator` varchar(64) DEFAULT NULL COMMENT '操作员',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消费记录表';

-- =====================================================
-- 8. 分享记录表
-- =====================================================
CREATE TABLE IF NOT EXISTS `share_records` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` bigint unsigned NOT NULL COMMENT '分享用户ID',
  `activity_id` bigint unsigned NOT NULL COMMENT '活动ID',
  `share_type` tinyint NOT NULL COMMENT '分享类型: 1好友 2朋友圈',
  `share_path` varchar(255) DEFAULT NULL COMMENT '分享路径',
  `click_count` int DEFAULT 0 COMMENT '点击次数',
  `new_user_count` int DEFAULT 0 COMMENT '带来新用户数',
  `chances_awarded` int DEFAULT 0 COMMENT '获得抽奖次数',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '分享时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_activity` (`user_id`, `activity_id`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分享记录表';

-- =====================================================
-- 9. 签到记录表
-- =====================================================
CREATE TABLE IF NOT EXISTS `checkin_records` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` bigint unsigned NOT NULL COMMENT '用户ID',
  `checkin_date` date NOT NULL COMMENT '签到日期',
  `consecutive_days` int DEFAULT 1 COMMENT '连续签到天数',
  `points_awarded` int DEFAULT 0 COMMENT '获得积分',
  `chances_awarded` int DEFAULT 0 COMMENT '获得抽奖次数',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '签到时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_date` (`user_id`, `checkin_date`),
  KEY `idx_checkin_date` (`checkin_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='签到记录表';

-- =====================================================
-- 10. 管理员表
-- =====================================================
CREATE TABLE IF NOT EXISTS `admins` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
  `username` varchar(64) NOT NULL COMMENT '用户名',
  `password` varchar(128) NOT NULL COMMENT '密码(加密)',
  `name` varchar(64) DEFAULT NULL COMMENT '姓名',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `role` tinyint DEFAULT 1 COMMENT '角色: 1普通管理员 2超级管理员',
  `permissions` json DEFAULT NULL COMMENT '权限配置',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` varchar(64) DEFAULT NULL COMMENT '最后登录IP',
  `status` tinyint DEFAULT 1 COMMENT '状态: 0禁用 1正常',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员表';

-- =====================================================
-- 初始化数据
-- =====================================================

-- 插入默认管理员 (密码: admin123，使用BCrypt加密)
INSERT INTO `admins` (`username`, `password`, `name`, `role`, `status`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '超级管理员', 2, 1);

-- 插入测试活动
INSERT INTO `activities` (`name`, `description`, `banner_url`, `start_time`, `end_time`, `daily_free_chances`, `new_user_chances`, `share_chances`, `daily_share_limit`, `guarantee_count`, `status`) VALUES
('新春抽奖活动', '参与抽奖赢好礼，新用户免费抽3次！', NULL, '2024-01-01 00:00:00', '2024-12-31 23:59:59', 1, 3, 1, 3, 10, 1);

-- 插入测试奖品（8个奖品对应转盘8个扇区）
INSERT INTO `prizes` (`activity_id`, `name`, `type`, `description`, `value`, `probability`, `total_stock`, `remaining_stock`, `is_guarantee`, `sort_order`, `coupon_config`, `status`) VALUES
(1, '电动牙刷', 2, '飞利浦电动牙刷一支', 299.00, 0.10, 10, 10, 0, 0, NULL, 1),
(1, '洗牙优惠券', 1, '洗牙服务50元优惠券', 50.00, 15.00, -1, -1, 1, 1, '{"discount_amount": 50, "min_amount": 0, "valid_days": 30, "applicable_services": ["洗牙"]}', 1),
(1, '口腔检查券', 1, '免费口腔检查一次', 100.00, 10.00, -1, -1, 1, 2, '{"discount_amount": 100, "min_amount": 0, "valid_days": 30, "applicable_services": ["口腔检查"]}', 1),
(1, '0.5元红包', 3, '微信红包0.5元', 0.50, 5.00, 1000, 1000, 0, 3, NULL, 1),
(1, '通用优惠券', 1, '消费满100减20优惠券', 20.00, 20.00, -1, -1, 1, 4, '{"discount_amount": 20, "min_amount": 100, "valid_days": 30, "applicable_services": ["全部"]}', 1),
(1, '牙膏套装', 2, '高露洁牙膏套装', 39.00, 1.00, 50, 50, 0, 5, NULL, 1),
(1, '1元红包', 3, '微信红包1元', 1.00, 2.00, 500, 500, 0, 6, NULL, 1),
(1, '谢谢参与', 4, '感谢您的参与，下次好运！', 0.00, 46.90, -1, -1, 0, 7, NULL, 1);
