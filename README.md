# Expressway Service Area System (Frontend)

本目录包含 Phase 1 的前端工程（Vue 3 + Vite + Element Plus + Pinia + Vue Router + Axios）。

## 运行

```bash
npm install
npm run dev
```

默认前端地址：`http://localhost:5173`

## 答辩演示

详见项目根目录 **[答辩演示.md](./答辩演示.md)**（含账号、演示步骤、话术要点）。

## 角色与权限（方案 A）

- 权限表：`sys_permission(role, permission)`，超管 `*` 表示全部权限
- 若库中仍是旧版 RBAC 结构，请执行：`sql/permissions_scheme_a.sql`
- 角色：`SUPER_ADMIN` / `OPERATOR` / `MERCHANT` / `USER`

## 后端联调

- 默认所有接口以 `/api` 开头，并在 `vite.config.js` 中代理到 `http://localhost:8080`
- 可通过环境变量修改（复制 `.env.example` 为 `.env` 并调整）：
  - `VITE_API_BASE_URL=/api`

## 数据库增量（新功能）

执行一次：

```bash
mysql -u root -p service_reservation_system < sql/feature_enhancements.sql
```

若已执行过 `permissions_scheme_a.sql`，可重新执行以包含 `order:log:view` 权限，或手动插入对应权限行。

## 已实现功能

- 登录页：`/login`（用户名 + 密码）
- JWT 存储：`localStorage.token`
- Axios 封装：请求/响应拦截器自动携带 JWT，401 自动踢回登录
- 路由守卫：未登录访问后台自动跳转登录
- 后台布局：侧边栏 + 顶部导航（用户名 + 退出）
- 个人中心：展示用户信息 + 修改密码
- 侧边栏菜单：按角色（`SUPER_ADMIN / OPERATOR / MERCHANT / USER`）显示
- 订单统计：侧边栏「统计分析 → 订单统计」
- 我的预约：USER 角色 `/my-reservations`
- 商品/商户图片上传：`ImageUpload` 组件 + `POST /files/upload`
- 订单操作日志：状态变更自动记录，订单详情时间线 + `/order-logs` 列表

## 接口路径（可按后端调整）

- 登录：`src/api/auth.js` → `POST /auth/login`
- 获取当前用户：`src/api/user.js` → `GET /users/me`
- 修改密码：`src/api/user.js` → `POST /users/change-password`

