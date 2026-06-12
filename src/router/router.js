import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../store/useUserStore'

import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import Layout from '../views/Layout.vue'
import UserInfoView from '../views/UserInfoView.vue'
import DashboardView from '../views/DashboardView.vue'
import AdminView from '../views/AdminView.vue'
import ServiceAreaManageView from '../views/ServiceAreaManageView.vue'
import MerchantManageView from '../views/MerchantManageView.vue'
import ProductManageView from '../views/ProductManageView.vue'
import OrderManageView from '../views/OrderManageView.vue'
import OrderStatisticsView from '../views/OrderStatisticsView.vue'
import ReservationManageView from '../views/ReservationManageView.vue'
import StatisticsView from '../views/StatisticsView.vue'
import UserManagement from '../views/UserManagement.vue'
import NearbyServiceAreaView from '../views/NearbyServiceAreaView.vue'
import MerchantReservationView from '../views/MerchantReservationView.vue'
import MyReservationsView from '../views/MyReservationsView.vue'
import OrderOperationLogView from '../views/OrderOperationLogView.vue'
import ForbiddenView from '../views/ForbiddenView.vue'
import { canAccessRoute } from '../config/menus'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: LoginView,
    meta: { public: true },
  },
  {
    path: '/register',
    name: 'Register',
    component: RegisterView,
    meta: { public: true },
  },
  {
    path: '/',
    component: Layout,
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        redirect: '/dashboard',
      },
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: DashboardView,
        meta: { title: '概览', requiresAuth: true, roles: ['SUPER_ADMIN', 'OPERATOR', 'MERCHANT', 'USER'] },
      },
      {
        path: 'user',
        name: 'UserInfo',
        component: UserInfoView,
        meta: { title: '个人中心', requiresAuth: true, roles: ['SUPER_ADMIN', 'OPERATOR', 'MERCHANT', 'USER'] },
      },
      {
        path: 'service-area',
        name: 'ServiceArea',
        component: ServiceAreaManageView,
        meta: { title: '服务区管理', requiresAuth: true, roles: ['SUPER_ADMIN', 'OPERATOR'] },
      },
      {
        path: 'merchant',
        name: 'Merchant',
        component: MerchantManageView,
        meta: { title: '商户管理', requiresAuth: true, roles: ['SUPER_ADMIN', 'OPERATOR'] },
      },
      {
        path: 'product',
        name: 'Product',
        component: ProductManageView,
        meta: { title: '商品管理', requiresAuth: true, roles: ['SUPER_ADMIN', 'OPERATOR', 'MERCHANT'] },
      },
      {
        path: 'order',
        name: 'Order',
        component: OrderManageView,
        meta: { title: '订单管理', requiresAuth: true, roles: ['SUPER_ADMIN', 'OPERATOR', 'MERCHANT', 'USER'] },
      },
      {
        path: 'order-statistics',
        name: 'OrderStatistics',
        component: OrderStatisticsView,
        meta: { title: '订单统计', requiresAuth: true, roles: ['SUPER_ADMIN', 'OPERATOR'] },
      },
      {
        path: 'book',
        name: 'Book',
        component: ReservationManageView,
        meta: { title: '我要预约', requiresAuth: true, roles: ['USER'] },
      },
      {
        path: 'my-reservations',
        name: 'MyReservations',
        component: MyReservationsView,
        meta: { title: '我的预约', requiresAuth: true, roles: ['USER'] },
      },
      {
        path: 'order-logs',
        name: 'OrderLogs',
        component: OrderOperationLogView,
        meta: { title: '操作日志', requiresAuth: true, roles: ['SUPER_ADMIN', 'OPERATOR', 'MERCHANT', 'USER'] },
      },
      {
        path: 'reservation-manage',
        name: 'ReservationManage',
        component: MerchantReservationView,
        meta: { title: '预约受理', requiresAuth: true, roles: ['SUPER_ADMIN', 'OPERATOR', 'MERCHANT'] },
      },
      {
        path: 'statistics',
        name: 'Statistics',
        component: StatisticsView,
        meta: { title: '统计分析', requiresAuth: true, roles: ['SUPER_ADMIN', 'OPERATOR'] },
      },
      {
        path: 'user-management',
        name: 'UserManagement',
        component: UserManagement,
        meta: { title: '用户管理', requiresAuth: true, roles: ['SUPER_ADMIN'] },
      },
      {
        path: 'nearby',
        name: 'Nearby',
        component: NearbyServiceAreaView,
        meta: { title: '附近服务区', requiresAuth: true, roles: ['USER'] },
      },
      {
        path: 'admin',
        name: 'Admin',
        component: AdminView,
        meta: { title: '系统设置', requiresAuth: true, roles: ['SUPER_ADMIN'] },
      },
      {
        path: '403',
        name: 'Forbidden',
        component: ForbiddenView,
        meta: { title: '无权限', requiresAuth: true },
      },
    ],
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/',
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach(async (to) => {
  const userStore = useUserStore()

  if (to.meta.public) {
    if ((to.path === '/login' || to.path === '/register') && userStore.isLoggedIn) return '/'
    return true
  }

  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }

  if (userStore.isLoggedIn) {
    const tokenRole = (() => {
      try {
        const parts = userStore.token.split('.')
        if (parts.length !== 3) return null
        const payload = JSON.parse(atob(parts[1].replace(/-/g, '+').replace(/_/g, '/')))
        return payload?.role || null
      } catch { return null }
    })()
    const storedRole = userStore.user?.role || null
    if (!userStore.user || (tokenRole && tokenRole !== storedRole)) {
      try {
        await userStore.fetchMe()
      } catch {
        // ignore
      }
    }

    const role = userStore.role || tokenRole || 'USER'
    if (to.path === '/reservation') {
      if (role === 'USER') return '/book'
      if (['MERCHANT', 'SUPER_ADMIN', 'OPERATOR'].includes(role)) return '/reservation-manage'
      return '/403'
    }

    if (to.meta.roles && !to.meta.roles.includes(role)) {
      return '/403'
    }
    if (!canAccessRoute(to.path, role) && to.path !== '/403') {
      return '/403'
    }
  }

  return true
})

export default router