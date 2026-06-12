/**
 * 菜单与路由角色配置（单一数据源）
 * roles: SUPER_ADMIN | OPERATOR | MERCHANT | USER
 */

export const MENU_ITEMS = [
  {
    path: '/dashboard',
    label: '概览',
    roles: ['SUPER_ADMIN', 'OPERATOR', 'MERCHANT', 'USER'],
  },
  {
    path: '/service-area',
    label: '服务区管理',
    roles: ['SUPER_ADMIN', 'OPERATOR'],
  },
  {
    path: '/merchant',
    label: '商户管理',
    roles: ['SUPER_ADMIN', 'OPERATOR'],
  },
  {
    path: '/product',
    label: '商品管理',
    roles: ['SUPER_ADMIN', 'OPERATOR', 'MERCHANT'],
  },
  {
    path: '/order',
    label: '订单管理',
    labelByRole: {
      USER: '我的订单',
      MERCHANT: '店铺订单',
      OPERATOR: '订单管理',
      SUPER_ADMIN: '订单管理',
    },
    roles: ['SUPER_ADMIN', 'OPERATOR', 'MERCHANT', 'USER'],
  },
  {
    path: '/book',
    label: '我要预约',
    roles: ['USER'],
  },
  {
    path: '/my-reservations',
    label: '我的预约',
    roles: ['USER'],
  },
  {
    path: '/reservation-manage',
    label: '预约受理',
    roles: ['SUPER_ADMIN', 'OPERATOR', 'MERCHANT'],
  },
  {
    path: '/statistics',
    label: '统计分析',
    roles: ['SUPER_ADMIN', 'OPERATOR'],
    children: [
      { path: '/statistics', label: '综合统计' },
      { path: '/order-statistics', label: '订单统计' },
    ],
  },
  {
    path: '/order-logs',
    label: '操作日志',
    roles: ['SUPER_ADMIN', 'OPERATOR', 'MERCHANT', 'USER'],
  },
  {
    path: '/system',
    label: '系统管理',
    roles: ['SUPER_ADMIN'],
    children: [{ path: '/user-management', label: '用户管理' }],
  },
  {
    path: '/nearby',
    label: '附近服务区',
    roles: ['USER'],
  },
  {
    path: '/user',
    label: '个人中心',
    roles: ['SUPER_ADMIN', 'OPERATOR', 'MERCHANT', 'USER'],
  },
]

export function getMenuLabel(item, role) {
  if (item.labelByRole && item.labelByRole[role]) {
    return item.labelByRole[role]
  }
  return item.label
}

export function filterMenusByRole(role) {
  const r = role || 'USER'
  return MENU_ITEMS.filter((m) => !m.roles || m.roles.includes(r))
}

/** 路由 path -> 允许访问的角色列表 */
export const ROUTE_ROLES = {
  '/dashboard': ['SUPER_ADMIN', 'OPERATOR', 'MERCHANT', 'USER'],
  '/service-area': ['SUPER_ADMIN', 'OPERATOR'],
  '/merchant': ['SUPER_ADMIN', 'OPERATOR'],
  '/product': ['SUPER_ADMIN', 'OPERATOR', 'MERCHANT'],
  '/order': ['SUPER_ADMIN', 'OPERATOR', 'MERCHANT', 'USER'],
  '/book': ['USER'],
  '/reservation-manage': ['SUPER_ADMIN', 'OPERATOR', 'MERCHANT'],
  '/my-reservations': ['USER'],
  '/statistics': ['SUPER_ADMIN', 'OPERATOR'],
  '/order-statistics': ['SUPER_ADMIN', 'OPERATOR'],
  '/order-logs': ['SUPER_ADMIN', 'OPERATOR', 'MERCHANT', 'USER'],
  '/user-management': ['SUPER_ADMIN'],
  '/nearby': ['USER'],
  '/user': ['SUPER_ADMIN', 'OPERATOR', 'MERCHANT', 'USER'],
  '/admin': ['SUPER_ADMIN'],
}

export function canAccessRoute(path, role) {
  const r = role || 'USER'
  const allowed = ROUTE_ROLES[path]
  if (!allowed) return true
  return allowed.includes(r)
}
