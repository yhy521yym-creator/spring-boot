-- 方案 A：角色 -> 权限标识（sys_permission.role + sys_permission.permission）
-- 若当前库中 sys_permission 为 RBAC 结构（name/code），请先备份后执行本脚本。

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role` varchar(20) NOT NULL COMMENT '角色',
  `permission` varchar(100) NOT NULL COMMENT '权限标识',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_permission` (`role`,`permission`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限表(方案A)';

INSERT INTO `sys_permission` (`role`, `permission`) VALUES
-- 超级管理员
('SUPER_ADMIN', '*'),

-- 运营管理员
('OPERATOR', 'service:view'),
('OPERATOR', 'service:edit'),
('OPERATOR', 'merchant:view'),
('OPERATOR', 'merchant:add'),
('OPERATOR', 'merchant:edit'),
('OPERATOR', 'merchant:audit'),
('OPERATOR', 'merchant:account'),
('OPERATOR', 'product:view'),
('OPERATOR', 'product:edit'),
('OPERATOR', 'order:view'),
('OPERATOR', 'order:edit'),
('OPERATOR', 'reservation:view'),
('OPERATOR', 'reservation:edit'),
('OPERATOR', 'reservation:delete'),
('OPERATOR', 'statistics:view'),
('OPERATOR', 'order:log:view'),

-- 商户
('MERCHANT', 'product:view'),
('MERCHANT', 'product:edit'),
('MERCHANT', 'order:view'),
('MERCHANT', 'order:edit'),
('MERCHANT', 'reservation:view'),
('MERCHANT', 'reservation:edit'),
('MERCHANT', 'order:log:view'),

-- 普通用户
('USER', 'order:view'),
('USER', 'order:create'),
('USER', 'reservation:view'),
('USER', 'reservation:create'),
('USER', 'nearby:view'),
('USER', 'order:log:view');

SET FOREIGN_KEY_CHECKS = 1;
