-- Disable foreign key checks to allow dropping tables
SET FOREIGN_KEY_CHECKS = 0;

-- Drop existing tables to ensure clean initialization
DROP TABLE IF EXISTS `user_role`;
DROP TABLE IF EXISTS `role_permission`;
DROP TABLE IF EXISTS `order`;
DROP TABLE IF EXISTS `product`;
DROP TABLE IF EXISTS `merchant`;
DROP TABLE IF EXISTS `sys_permission`;
DROP TABLE IF EXISTS `sys_role`;
DROP TABLE IF EXISTS `sys_user`;
DROP TABLE IF EXISTS `service_area`;
DROP TABLE IF EXISTS `reservation`;

-- Enable foreign key checks again
SET FOREIGN_KEY_CHECKS = 1;

-- Create tables and insert data
CREATE TABLE IF NOT EXISTS service_area (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(500),
    region VARCHAR(50),
    longitude DECIMAL(15,10),
    latitude DECIMAL(15,10),
    facilities VARCHAR(500),
    description TEXT,
    phone VARCHAR(20),
    status INT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO service_area (id, name, address, region, longitude, latitude, facilities, description, phone, status) VALUES
(1, 'G2京沪高速无锡服务区', '江苏省无锡市锡山区G2京沪高速', '无锡', 120.3000000000, 31.5000000000, '餐饮,加油,停车,购物', '大型综合服务区', '0510-88888801', 1),
(2, 'G42沪宁高速苏州服务区', '江苏省苏州市昆山市G42沪宁高速', '苏州', 120.9000000000, 31.3000000000, '餐饮,加油,停车,休息', '综合服务区', '0512-88888802', 1),
(3, 'G60沪杭高速嘉兴服务区', '浙江省嘉兴市秀洲区G60沪杭高速', '嘉兴', 120.5000000000, 30.7000000000, '餐饮,加油,停车,购物', '中型服务区', '0573-88888803', 1),
(4, 'G15沈海高速常熟服务区', '江苏省苏州市常熟市G15沈海高速', '常熟', 120.7000000000, 31.6000000000, '餐饮,加油,停车', '标准服务区', '0512-88888804', 1),
(5, 'G25长深高速湖州服务区', '浙江省湖州市吴兴区G25长深高速', '湖州', 120.1000000000, 30.9000000000, '餐饮,加油,停车,休息', '综合服务区', '0572-88888805', 1),
(6, 'S58沪常高速淀山湖服务区', '上海市青浦区S58沪常高速', '上海', 121.0000000000, 31.2000000000, '餐饮,加油,停车,购物', '大型服务区', '021-88888806', 1),
(7, 'G2京沪高速江阴服务区', '江苏省无锡市江阴市G2京沪高速', '江阴', 120.2000000000, 31.9000000000, '餐饮,加油,停车', '标准服务区', '0510-88888807', 1),
(8, 'G60沪杭高速长安服务区', '浙江省嘉兴市海宁市G60沪杭高速', '海宁', 120.4000000000, 30.5000000000, '餐饮,加油,停车,购物', '中型服务区', '0573-88888808', 1),
(9, 'S26沪常高速嘉善服务区', '浙江省嘉兴市嘉善县S26沪常高速', '嘉善', 120.9000000000, 30.8000000000, '餐饮,加油,停车', '标准服务区', '0573-88888809', 1),
(10, 'G15沈海高速太仓服务区', '江苏省苏州市太仓市G15沈海高速', '太仓', 121.1000000000, 31.5000000000, '餐饮,加油,停车', '标准服务区', '0512-88888810', 1),
(11, '测试服务区', '沧州市交通学院', '沧州', 117.0000000000, 38.3000000000, '餐饮,加油,停车', '测试用服务区', '13800138000', 1);

CREATE TABLE IF NOT EXISTS merchant (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    service_area_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL,
    description VARCHAR(500),
    image_url VARCHAR(500),
    address VARCHAR(500),
    contact_name VARCHAR(50),
    contact_phone VARCHAR(20),
    business_hours VARCHAR(100),
    capacity INT,
    status INT DEFAULT 1,
    audit_time TIMESTAMP NULL,
    auditor_id BIGINT NULL,
    audit_remark VARCHAR(500),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (service_area_id) REFERENCES service_area(id) ON DELETE CASCADE
);

INSERT INTO merchant (id, service_area_id, name, type, description, contact_phone, business_hours, capacity, status) VALUES
(1, 1, '太湖餐厅', 'RESTAURANT', '提供江浙特色美食', '13800138001', '09:00-21:00', 80, 1),
(2, 1, '快捷酒店', 'HOTEL', '经济型快捷酒店', '13800138002', '24小时营业', 60, 1),
(3, 3, '江南小厨', 'RESTAURANT', '杭帮菜特色餐厅', '13800138003', '11:00-22:00', 70, 1),
(4, 2, '商务酒店', 'HOTEL', '商务型酒店，配套齐全', '13800138004', '24小时营业', 80, 1),
(5, 3, '杭州酒家', 'RESTAURANT', '正宗杭帮菜，味道鲜美', '13800138005', '09:30-21:30', 120, 1),
(6, 4, '苏味轩', 'RESTAURANT', '苏州特色美食餐厅', '13800138006', '11:00-22:00', 90, 1),
(7, 5, '湖州鱼庄', 'RESTAURANT', '湖鲜特色餐厅', '13800138007', '10:00-21:00', 70, 1),
(8, 6, '上海小吃', 'RESTAURANT', '上海特色小吃汇聚', '13800138008', '08:00-22:00', 60, 1),
(9, 7, '江阴菜馆', 'RESTAURANT', '本地特色家常菜', '13800138009', '11:00-20:30', 85, 1),
(10, 8, '长安美食', 'RESTAURANT', '杭帮菜与本地特色结合', '13800138010', '09:00-21:00', 100, 1),
(77, 11, '一期食堂', 'RESTAURANT', '一期食堂商户', '13800138077', '06:00-22:00', 200, 1);

CREATE TABLE IF NOT EXISTS product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    merchant_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL,
    description VARCHAR(500),
    price DECIMAL(10,2) NOT NULL,
    image_url VARCHAR(500),
    stock INT DEFAULT 100,
    status INT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (merchant_id) REFERENCES merchant(id) ON DELETE CASCADE
);

INSERT INTO product (id, merchant_id, name, type, description, price, stock, status) VALUES
(1, 1, '太湖银鱼炒蛋', 'RESTAURANT', '新鲜银鱼配土鸡蛋，鲜香嫩滑', 48.00, 50, 1),
(2, 1, '无锡排骨', 'RESTAURANT', '无锡特色酱排骨，甜而不腻', 68.00, 40, 1),
(3, 1, '清蒸太湖白鱼', 'RESTAURANT', '太湖野生白鱼，肉质鲜美', 88.00, 30, 1),
(4, 3, '西湖醋鱼', 'RESTAURANT', '杭州名菜，酸甜适口', 58.00, 45, 1),
(5, 3, '东坡肉', 'RESTAURANT', '肥而不腻，入口即化', 42.00, 50, 1),
(6, 3, '龙井虾仁', 'RESTAURANT', '鲜嫩虾仁配龙井茶香', 78.00, 35, 1),
(7, 5, '叫化鸡', 'RESTAURANT', '传统工艺烤制，香气四溢', 98.00, 25, 1),
(8, 5, '宋嫂鱼羹', 'RESTAURANT', '杭州传统名菜，酸辣鲜香', 38.00, 50, 1),
(9, 6, '松鼠鳜鱼', 'RESTAURANT', '苏州名菜，外酥里嫩', 128.00, 20, 1),
(10, 6, '响油鳝糊', 'RESTAURANT', '苏州特色，鲜嫩爽滑', 68.00, 30, 1),
(11, 7, '太湖蟹', 'RESTAURANT', '新鲜大闸蟹，膏肥黄满', 168.00, 15, 1),
(12, 8, '生煎包', 'RESTAURANT', '上海特色，皮薄汁多', 28.00, 100, 1),
(13, 8, '小笼包', 'RESTAURANT', '南翔小笼，鲜美多汁', 32.00, 80, 1),
(14, 9, '刀鱼馄饨', 'RESTAURANT', '江阴特色，鲜美无比', 48.00, 40, 1),
(15, 10, '片儿川', 'RESTAURANT', '杭州特色面，鲜香爽口', 28.00, 60, 1),
(16, 2, '标准间', 'HOTEL', '舒适标准间，含双早', 188.00, 30, 1),
(17, 2, '大床房', 'HOTEL', '温馨大床房，含双早', 228.00, 15, 1),
(18, 2, '家庭房', 'HOTEL', '宽敞家庭房，含三早', 328.00, 5, 1),
(19, 4, '商务标间', 'HOTEL', '商务标准间，含双早，免费WiFi', 268.00, 40, 1),
(20, 4, '商务套房', 'HOTEL', '豪华商务套房，含双早', 468.00, 10, 1),
(21, 1, '蛋炒饭', 'RESTAURANT', '粒粒分明，蛋香浓郁', 18.00, 100, 1),
(22, 1, '酸辣土豆丝', 'RESTAURANT', '酸辣爽口', 12.00, 50, 1),
(23, 1, '番茄炒蛋', 'RESTAURANT', '经典家常菜', 16.00, 50, 1),
(24, 1, '矿泉水550ml', 'GENERAL', '瓶装矿泉水', 2.00, 500, 1),
(25, 1, '可乐500ml', 'GENERAL', '碳酸饮料', 3.50, 300, 1),
(26, 1, '方便面', 'GENERAL', '速食泡面', 4.50, 200, 1),
(27, 1, '拿铁咖啡', 'RESTAURANT', '香浓拿铁', 28.00, 50, 1),
(28, 1, '提拉米苏', 'RESTAURANT', '意大利甜点', 32.00, 30, 1),
(29, 1, '无锡酱排骨礼盒', 'GENERAL', '特色礼盒装', 128.00, 50, 1),
(30, 1, '太湖银鱼干', 'GENERAL', '太湖特产', 68.00, 80, 1),
(31, 3, '松鼠鳜鱼', 'RESTAURANT', '苏州名菜', 128.00, 20, 1),
(32, 3, '响油鳝糊', 'RESTAURANT', '鲜嫩爽滑', 68.00, 30, 1),
(33, 3, '清炒虾仁', 'RESTAURANT', '鲜嫩可口', 58.00, 40, 1),
(34, 3, '矿泉水550ml', 'GENERAL', '瓶装水', 2.00, 400, 1),
(35, 3, '零食大礼包', 'GENERAL', '各类零食', 45.00, 100, 1),
(36, 3, '苏式汤面', 'RESTAURANT', '正宗苏式面', 28.00, 50, 1),
(37, 3, '焖肉面', 'RESTAURANT', '经典焖肉', 32.00, 40, 1),
(38, 3, '碧螺春', 'RESTAURANT', '苏州名茶', 188.00, 20, 1),
(39, 3, '蟹粉小笼', 'RESTAURANT', '鲜美多汁', 38.00, 60, 1),
(40, 3, '苏绣围巾', 'GENERAL', '精美刺绣', 168.00, 30, 1),
(41, 5, '西湖醋鱼', 'RESTAURANT', '酸甜适口', 58.00, 45, 1),
(42, 5, '东坡肉', 'RESTAURANT', '肥而不腻', 42.00, 50, 1),
(43, 5, '龙井虾仁', 'RESTAURANT', '茶香虾仁', 78.00, 35, 1),
(44, 5, '饮料组合', 'GENERAL', '多种饮料', 25.00, 150, 1),
(45, 5, '方便面组合', 'GENERAL', '多种口味', 30.00, 100, 1),
(46, 5, '鲜肉粽', 'RESTAURANT', '嘉兴肉粽', 12.00, 100, 1),
(47, 5, '蛋黄粽', 'RESTAURANT', '蛋黄肉粽', 15.00, 80, 1),
(48, 5, '定胜糕', 'RESTAURANT', '江南传统糕点', 18.00, 60, 1),
(49, 5, '条头糕', 'RESTAURANT', '软糯香甜', 12.00, 80, 1),
(50, 5, '嘉兴蜜梨', 'GENERAL', '新鲜蜜梨', 15.00, 100, 1),
(51, 6, '叫化鸡', 'RESTAURANT', '传统工艺', 98.00, 25, 1),
(52, 6, '鸭血粉丝汤', 'RESTAURANT', '南京特色', 28.00, 60, 1),
(53, 6, '盐水鸭', 'RESTAURANT', '南京名菜', 48.00, 40, 1),
(54, 6, '零食小吃', 'GENERAL', '各类零食', 20.00, 200, 1),
(55, 6, '矿泉水', 'GENERAL', '瓶装水', 2.00, 500, 1),
(56, 6, '奥灶面', 'RESTAURANT', '昆山特色', 35.00, 40, 1),
(57, 6, '爆鱼面', 'RESTAURANT', '鱼香浓郁', 32.00, 45, 1),
(58, 6, '桂花酒', 'GENERAL', '常熟特产', 88.00, 60, 1),
(59, 6, '鸭血', 'GENERAL', '新鲜鸭血', 15.00, 100, 1),
(60, 6, '美式咖啡', 'RESTAURANT', '经典美式', 22.00, 60, 1),
(121, 77, '大饼卷肉', 'RESTAURANT', '传统大饼卷肉，美味可口', 10.00, 64, 1);

CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100),
    avatar VARCHAR(255),
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    merchant_id BIGINT,
    status INT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (merchant_id) REFERENCES merchant(id)
);

INSERT INTO sys_user (id, username, password, phone, role, merchant_id, status) VALUES
(1, 'admin', '$2a$10$IPop.7qBsmUGt1VQcmsVen4VHgnfFZfAWONJmjiijUH23qJAH27m', '13800138000', 'SUPER_ADMIN', NULL, 1),
(2, 'ceshi1', '$2a$10$IPop.7qBsmUGt1VQcmsVen4VHgnfFZfAWONJmjiijUH23qJAH27m', '13800138001', 'MERCHANT', 77, 1),
(3, 'yonghu1', '$2a$10$IPop.7qBsmUGt1VQcmsVen4VHgnfFZfAWONJmjiijUH23qJAH27m', '13800138002', 'USER', NULL, 1),
(4, 'operator', '$2a$10$IPop.7qBsmUGt1VQcmsVen4VHgnfFZfAWONJmjiijUH23qJAH27m', '13800138003', 'OPERATOR', NULL, 1);

CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    code VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(500),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO sys_role (id, name, code, description) VALUES
(1, '超级管理员', 'SUPER_ADMIN', '系统超级管理员，拥有所有权限'),
(2, '操作员', 'OPERATOR', '系统操作员，拥有部分管理权限'),
(3, '商户', 'MERCHANT', '商户用户，管理自己的商品和订单'),
(4, '普通用户', 'USER', '普通用户，可进行预约');

CREATE TABLE IF NOT EXISTS user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES sys_user(id),
    FOREIGN KEY (role_id) REFERENCES sys_role(id),
    UNIQUE KEY (user_id, role_id)
);

INSERT INTO user_role (user_id, role_id) VALUES
(1, 1),
(2, 3),
(3, 4),
(4, 2);

CREATE TABLE IF NOT EXISTS sys_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(500),
    parent_id BIGINT,
    sort_order INT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO sys_permission (id, name, code, description, parent_id, sort_order) VALUES
(1, '服务区管理', 'servicearea:manage', '服务区管理权限', NULL, 1),
(2, '商户管理', 'merchant:manage', '商户管理权限', NULL, 2),
(3, '商品管理', 'product:manage', '商品管理权限', NULL, 3),
(4, '订单管理', 'order:manage', '订单管理权限', NULL, 4),
(5, '用户管理', 'user:manage', '用户管理权限', NULL, 5),
(6, '系统设置', 'system:manage', '系统设置权限', NULL, 6),
(7, '商户审核', 'merchant:audit', '商户审核权限', NULL, 7);

CREATE TABLE IF NOT EXISTS role_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    FOREIGN KEY (role_id) REFERENCES sys_role(id),
    FOREIGN KEY (permission_id) REFERENCES sys_permission(id),
    UNIQUE KEY (role_id, permission_id)
);

INSERT INTO role_permission (role_id, permission_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7),
(2, 1), (2, 2), (2, 3), (2, 4), (2, 6), (2, 7),
(3, 3), (3, 4),
(4, 4);

CREATE TABLE IF NOT EXISTS `order` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(50) UNIQUE,
    user_id BIGINT NOT NULL,
    merchant_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    reservation_date DATE,
    reservation_time TIME,
    contact_name VARCHAR(50),
    contact_phone VARCHAR(20),
    remark VARCHAR(500),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES sys_user(id),
    FOREIGN KEY (merchant_id) REFERENCES merchant(id),
    FOREIGN KEY (product_id) REFERENCES product(id)
);

INSERT INTO `order` (id, order_no, user_id, merchant_id, product_id, quantity, total_price, status, reservation_date, contact_name, contact_phone) VALUES
(1, 'ORD202605200001', 3, 77, 121, 2, 20.00, 'PENDING', '2026-05-20', 'yonghu1', '13800138002'),
(2, 'ORD202605190002', 3, 1, 1, 1, 48.00, 'CONFIRMED', '2026-05-19', 'yonghu1', '13800138002'),
(3, 'ORD202605180003', 3, 1, 2, 1, 68.00, 'COMPLETED', '2026-05-18', 'yonghu1', '13800138002'),
(4, 'ORD202605170004', 3, 3, 4, 2, 116.00, 'CANCELLED', '2026-05-17', 'yonghu1', '13800138002');

CREATE TABLE IF NOT EXISTS reservation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    merchant_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    order_id BIGINT NULL,
    user_name VARCHAR(50),
    user_phone VARCHAR(20),
    reservation_date DATE,
    reservation_time TIME,
    duration INT,
    quantity INT DEFAULT 1,
    status VARCHAR(20) DEFAULT 'PENDING',
    remark VARCHAR(500),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (merchant_id) REFERENCES merchant(id),
    FOREIGN KEY (user_id) REFERENCES sys_user(id)
);

INSERT INTO reservation (id, merchant_id, user_id, user_name, user_phone, reservation_date, reservation_time, duration, quantity, status) VALUES
(1, 77, 3, 'yonghu1', '13800138002', '2026-05-21', '10:00:00', 60, 2, 'PENDING'),
(2, 1, 3, 'yonghu1', '13800138002', '2026-05-20', '12:00:00', 90, 4, 'CONFIRMED'),
(3, 3, 3, 'yonghu1', '13800138002', '2026-05-19', '18:00:00', 120, 3, 'COMPLETED');

ALTER TABLE merchant AUTO_INCREMENT = 78;
ALTER TABLE product AUTO_INCREMENT = 122;
ALTER TABLE `order` AUTO_INCREMENT = 5;
ALTER TABLE reservation AUTO_INCREMENT = 4;
