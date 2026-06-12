-- 预约与订单关联（方案 B）
SET NAMES utf8mb4;

ALTER TABLE reservation ADD COLUMN order_id BIGINT NULL COMMENT '关联订单ID' AFTER user_id;
ALTER TABLE reservation ADD INDEX idx_reservation_order_id (order_id);
