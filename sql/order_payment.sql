-- 订单模拟支付字段
SET NAMES utf8mb4;

ALTER TABLE `order`
    ADD COLUMN pay_status VARCHAR(20) NOT NULL DEFAULT 'UNPAID' COMMENT 'UNPAID未支付 PAID已支付' AFTER status,
    ADD COLUMN pay_time TIMESTAMP NULL COMMENT '支付时间' AFTER pay_status,
    ADD COLUMN pay_method VARCHAR(32) NULL COMMENT 'WECHAT/ALIPAY/SIMULATE' AFTER pay_time;

-- 已完成/已确认/已取消的历史订单视为已支付；待确认订单保持待支付
UPDATE `order` SET pay_status = 'PAID', pay_method = 'LEGACY'
WHERE status IN ('CONFIRMED', 'COMPLETED', 'CANCELLED');
UPDATE `order` SET pay_status = 'UNPAID', pay_method = NULL
WHERE status = 'PENDING';
