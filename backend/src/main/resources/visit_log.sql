-- 访问记录表
CREATE TABLE IF NOT EXISTS visit_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    service_area_id BIGINT NOT NULL,
    user_id BIGINT,
    ip_address VARCHAR(50),
    visit_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (service_area_id) REFERENCES service_area(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建索引提升查询效率
CREATE INDEX idx_visit_service_area ON visit_log(service_area_id);
CREATE INDEX idx_visit_time ON visit_log(visit_time);
