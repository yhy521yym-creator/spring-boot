-- 高速公路服务区服务系统（第一阶段 - 第一步）
-- 目标：创建用户与权限相关表，并插入初始数据
-- 适用：MySQL 8.0

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 用户表（存储管理员/用户信息）
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码（加密存储）',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `role` varchar(20) NOT NULL DEFAULT 'OPERATOR' COMMENT '角色：SUPER_ADMIN/OPERATOR/MERCHANT',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- ----------------------------
-- 权限表（存储角色与权限映射）
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `role` varchar(20) NOT NULL COMMENT '角色',
  `permission` varchar(100) NOT NULL COMMENT '权限标识（如：service:view）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_permission` (`role`,`permission`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限表';

-- ----------------------------
-- 初始数据
-- 超级管理员账号：admin
-- 密码：123456（BCrypt）
-- ----------------------------
INSERT INTO `sys_user` (`username`, `password`, `role`)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iCtKBUy', 'SUPER_ADMIN');

INSERT INTO `sys_permission` (`role`, `permission`) VALUES
('SUPER_ADMIN', '*'),
('OPERATOR', 'service:view'),
('OPERATOR', 'service:edit'),
('OPERATOR', 'merchant:view'),
('OPERATOR', 'merchant:add'),
('OPERATOR', 'merchant:edit'),
('OPERATOR', 'merchant:audit'),
('OPERATOR', 'product:view'),
('OPERATOR', 'product:edit'),
('OPERATOR', 'order:view'),
('OPERATOR', 'order:edit'),
('OPERATOR', 'reservation:view'),
('OPERATOR', 'reservation:edit'),
('OPERATOR', 'reservation:delete'),
('OPERATOR', 'statistics:view'),
('MERCHANT', 'product:view'),
('MERCHANT', 'product:edit'),
('MERCHANT', 'order:view'),
('MERCHANT', 'order:edit'),
('MERCHANT', 'reservation:view'),
('MERCHANT', 'reservation:edit'),
('USER', 'order:view'),
('USER', 'order:create'),
('USER', 'reservation:view'),
('USER', 'reservation:create'),
('USER', 'nearby:view');

SET FOREIGN_KEY_CHECKS = 1;


