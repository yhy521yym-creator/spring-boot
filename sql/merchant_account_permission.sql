-- 运营管理员可为商户店铺创建/绑定登录账号
INSERT IGNORE INTO sys_permission (role, permission) VALUES
('OPERATOR', 'merchant:account');
