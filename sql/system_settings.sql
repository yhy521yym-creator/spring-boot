-- 系统设置模块数据库脚本

-- 1. 创建角色表（如果不存在）
CREATE TABLE IF NOT EXISTS role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    code VARCHAR(100) NOT NULL UNIQUE,
    status TINYINT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 2. 如果role表存在但缺少code字段，添加code字段
ALTER TABLE role ADD COLUMN IF NOT EXISTS code VARCHAR(100) NOT NULL UNIQUE COMMENT '角色编码' AFTER name;

-- 3. 创建权限表
CREATE TABLE IF NOT EXISTS permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    code VARCHAR(100) NOT NULL UNIQUE,
    type VARCHAR(20) NOT NULL,
    parent_id BIGINT DEFAULT 0,
    path VARCHAR(200),
    component VARCHAR(200),
    icon VARCHAR(50),
    sort INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 4. 创建角色-权限关联表
CREATE TABLE IF NOT EXISTS role_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permission(id) ON DELETE CASCADE,
    UNIQUE KEY uk_role_permission (role_id, permission_id)
);

-- 5. 创建用户-角色关联表
CREATE TABLE IF NOT EXISTS user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_role (user_id, role_id)
);

-- 6. 创建站点配置表
CREATE TABLE IF NOT EXISTS site_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    config_key VARCHAR(100) NOT NULL UNIQUE,
    config_value TEXT,
    description VARCHAR(200),
    status TINYINT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 7. 插入角色数据
INSERT INTO role (name, code, status) VALUES
('超级管理员', 'ROLE_SUPER_ADMIN', 1),
('普通管理员', 'ROLE_ADMIN', 1),
('操作员', 'ROLE_OPERATOR', 1),
('商户', 'ROLE_MERCHANT', 1)
ON DUPLICATE KEY UPDATE name = VALUES(name), status = VALUES(status);

-- 8. 插入权限树数据
-- 系统设置菜单
INSERT INTO permission (name, code, type, parent_id, path, component, icon, sort, status) VALUES
('系统设置', 'system:menu', 'menu', 0, '/system', 'Layout', 'Setting', 1, 1),
('用户管理', 'user:menu', 'menu', 1, '/system/user', 'system/user/index', 'User', 1, 1),
('角色管理', 'role:menu', 'menu', 1, '/system/role', 'system/role/index', 'Avatar', 2, 1),
('权限管理', 'permission:menu', 'menu', 1, '/system/permission', 'system/permission/index', 'Key', 3, 1),
('站点配置', 'config:menu', 'menu', 1, '/system/config', 'system/config/index', 'Monitor', 4, 1),

-- 用户管理按钮权限
('用户列表', 'user:list', 'button', 2, NULL, NULL, NULL, 1, 1),
('新增用户', 'user:add', 'button', 2, NULL, NULL, NULL, 2, 1),
('编辑用户', 'user:edit', 'button', 2, NULL, NULL, NULL, 3, 1),
('删除用户', 'user:delete', 'button', 2, NULL, NULL, NULL, 4, 1),

-- 角色管理按钮权限
('角色列表', 'role:list', 'button', 3, NULL, NULL, NULL, 1, 1),
('新增角色', 'role:add', 'button', 3, NULL, NULL, NULL, 2, 1),
('编辑角色', 'role:edit', 'button', 3, NULL, NULL, NULL, 3, 1),
('删除角色', 'role:delete', 'button', 3, NULL, NULL, NULL, 4, 1),
('分配权限', 'role:assign', 'button', 3, NULL, NULL, NULL, 5, 1),

-- 权限管理按钮权限
('权限列表', 'permission:list', 'button', 4, NULL, NULL, NULL, 1, 1),
('新增权限', 'permission:add', 'button', 4, NULL, NULL, NULL, 2, 1),
('编辑权限', 'permission:edit', 'button', 4, NULL, NULL, NULL, 3, 1),
('删除权限', 'permission:delete', 'button', 4, NULL, NULL, NULL, 4, 1),

-- 站点配置按钮权限
('配置列表', 'config:list', 'button', 5, NULL, NULL, NULL, 1, 1),
('新增配置', 'config:add', 'button', 5, NULL, NULL, NULL, 2, 1),
('编辑配置', 'config:edit', 'button', 5, NULL, NULL, NULL, 3, 1),
('删除配置', 'config:delete', 'button', 5, NULL, NULL, NULL, 4, 1)
ON DUPLICATE KEY UPDATE name = VALUES(name), code = VALUES(code), type = VALUES(type), parent_id = VALUES(parent_id), path = VALUES(path), component = VALUES(component), icon = VALUES(icon), sort = VALUES(sort), status = VALUES(status);

-- 9. 为超级管理员角色分配所有权限
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM role r, permission p
WHERE r.code = 'ROLE_SUPER_ADMIN'
  AND NOT EXISTS (
    SELECT 1 FROM role_permission rp WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

-- 10. 为普通管理员角色分配部分权限
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM role r, permission p
WHERE r.code = 'ROLE_ADMIN'
  AND p.code IN (
    'system:menu', 'user:menu', 'role:menu', 'config:menu',
    'user:list', 'user:add', 'user:edit',
    'role:list', 'role:add', 'role:edit',
    'config:list', 'config:add', 'config:edit'
  )
  AND NOT EXISTS (
    SELECT 1 FROM role_permission rp WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

-- 11. 为操作员角色分配有限权限
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM role r, permission p
WHERE r.code = 'ROLE_OPERATOR'
  AND p.code IN (
    'system:menu', 'config:menu',
    'config:list', 'config:edit'
  )
  AND NOT EXISTS (
    SELECT 1 FROM role_permission rp WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

-- 12. 为现有用户分配角色
-- 为admin用户分配超级管理员角色
INSERT INTO user_role (user_id, role_id)
SELECT u.id, r.id
FROM sys_user u, role r
WHERE u.username = 'admin' AND r.code = 'ROLE_SUPER_ADMIN'
  AND NOT EXISTS (
    SELECT 1 FROM user_role ur WHERE ur.user_id = u.id AND ur.role_id = r.id
  );

-- 13. 插入站点配置数据
INSERT INTO site_config (config_key, config_value, description, status) VALUES
('map.api.key', '您的高德地图API密钥', '高德地图API密钥', 1),
('site.title', '高速公路服务区管理系统', '系统标题', 1),
('site.footer', '© 2026 高速公路服务区管理系统', '系统页脚', 1),
('upload.path', '/uploads', '文件上传路径', 1),
('page.size', '10', '默认分页大小', 1)
ON DUPLICATE KEY UPDATE config_value = VALUES(config_value), description = VALUES(description), status = VALUES(status);

-- 14. 更新sys_user表，添加status字段（如果不存在）
ALTER TABLE sys_user ADD COLUMN IF NOT EXISTS status TINYINT DEFAULT 1;

-- 15. 更新sys_user表，添加email字段（如果不存在）
ALTER TABLE sys_user ADD COLUMN IF NOT EXISTS email VARCHAR(100);

-- 15. 更新sys_user表，添加merchantId字段（如果不存在）
ALTER TABLE sys_user ADD COLUMN IF NOT EXISTS merchant_id BIGINT;

-- 16. 更新现有用户的email和status字段
UPDATE sys_user SET 
    email = 'admin@example.com',
    status = 1
WHERE username = 'admin';
