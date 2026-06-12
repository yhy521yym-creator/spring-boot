-- 将所有演示账号密码重置为 123456（BCrypt，由 Spring BCryptPasswordEncoder 生成）
UPDATE sys_user SET password = '$2a$10$oIu.f1iJDiSx7Xghdc4sZeGkfMwMXOzbRICuXop0bZMB3gRJJad0e'
WHERE username IN ('admin', 'operator', 'ceshi1', 'yonghu1', 'ceshi2');
