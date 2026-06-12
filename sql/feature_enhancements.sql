-- 功能增强：商户图片、订单操作日志
SET NAMES utf8mb4;

-- 商户封面图（若列已存在可忽略报错）
ALTER TABLE merchant ADD COLUMN image_url VARCHAR(500) NULL COMMENT '商户图片' AFTER description;

DROP TABLE IF EXISTS order_operation_log;
CREATE TABLE order_operation_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    order_no VARCHAR(64) NOT NULL,
    user_id BIGINT NULL COMMENT '订单所属用户',
    merchant_id BIGINT NULL COMMENT '订单所属商户',
    operator_id BIGINT NOT NULL,
    operator_username VARCHAR(64) NOT NULL,
    operator_role VARCHAR(32) NOT NULL,
    old_status VARCHAR(32) NULL,
    new_status VARCHAR(32) NOT NULL,
    remark VARCHAR(255) NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_order_id (order_id),
    INDEX idx_merchant_id (merchant_id),
    INDEX idx_user_id (user_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单状态操作日志';
