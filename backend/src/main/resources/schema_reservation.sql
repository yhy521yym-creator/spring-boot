-- 商户表（餐饮店、酒店、充电桩、加油站）
CREATE TABLE IF NOT EXISTS merchant (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    service_area_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL COMMENT '商户名称',
    type VARCHAR(20) NOT NULL COMMENT '类型：RESTAURANT-餐饮, HOTEL-酒店, CHARGING-充电桩, GAS_STATION-加油站',
    description VARCHAR(500) COMMENT '描述',
    address VARCHAR(200) COMMENT '地址',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    business_hours VARCHAR(100) COMMENT '营业时间',
    capacity INT COMMENT '容量/数量',
    status INT DEFAULT 1 COMMENT '状态：0-停用 1-启用',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (service_area_id) REFERENCES service_area(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 预约表
CREATE TABLE IF NOT EXISTS reservation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    merchant_id BIGINT NOT NULL,
    user_id BIGINT COMMENT '预约用户ID',
    user_name VARCHAR(50) COMMENT '预约人姓名',
    user_phone VARCHAR(20) COMMENT '预约人电话',
    reservation_date DATE NOT NULL COMMENT '预约日期',
    reservation_time TIME COMMENT '预约时间',
    duration INT COMMENT '预约时长（分钟）',
    quantity INT DEFAULT 1 COMMENT '预约数量',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态：PENDING-待确认, CONFIRMED-已确认, CANCELLED-已取消, COMPLETED-已完成',
    remark VARCHAR(500) COMMENT '备注',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (merchant_id) REFERENCES merchant(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 索引
CREATE INDEX IF NOT EXISTS idx_merchant_type ON merchant(type);
CREATE INDEX IF NOT EXISTS idx_merchant_service_area ON merchant(service_area_id);
CREATE INDEX IF NOT EXISTS idx_reservation_date ON reservation(reservation_date);
CREATE INDEX IF NOT EXISTS idx_reservation_status ON reservation(status);
CREATE INDEX IF NOT EXISTS idx_reservation_user ON reservation(user_id);
