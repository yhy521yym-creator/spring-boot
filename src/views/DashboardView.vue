<template>
  <div class="page-view dashboard">
    <el-row :gutter="16" class="stats-row">
      <template v-if="isPlatformAdmin">
        <el-col :xs="24" :sm="12" :md="6">
          <el-card shadow="never" class="stat-card page-card service-areas">
            <div class="stat-content">
              <div class="stat-icon"><el-icon><Location /></el-icon></div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.serviceAreaCount }}</div>
                <div class="stat-label">服务区</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-card shadow="never" class="stat-card page-card merchants">
            <div class="stat-content">
              <div class="stat-icon"><el-icon><Shop /></el-icon></div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.merchantCount }}</div>
                <div class="stat-label">商户</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-card shadow="never" class="stat-card page-card orders">
            <div class="stat-content">
              <div class="stat-icon"><el-icon><Document /></el-icon></div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.orderCount }}</div>
                <div class="stat-label">订单</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-card shadow="never" class="stat-card page-card users">
            <div class="stat-content">
              <div class="stat-icon"><el-icon><User /></el-icon></div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.userCount }}</div>
                <div class="stat-label">用户</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </template>
      <template v-else-if="isMerchant">
        <el-col :xs="24" :sm="8">
          <el-card shadow="never" class="stat-card page-card orders">
            <div class="stat-content">
              <div class="stat-value">{{ stats.myOrderCount }}</div>
              <div class="stat-label">店铺订单</div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="8">
          <el-card shadow="never" class="stat-card page-card orders">
            <div class="stat-content">
              <div class="stat-value">{{ stats.pendingOrderCount }}</div>
              <div class="stat-label">待处理订单</div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="8">
          <el-card shadow="never" class="stat-card page-card users">
            <div class="stat-content">
              <div class="stat-value">{{ stats.myReservationCount }}</div>
              <div class="stat-label">预约数</div>
            </div>
          </el-card>
        </el-col>
      </template>
      <template v-else>
        <el-col :xs="24" :sm="12">
          <el-card shadow="never" class="stat-card page-card orders">
            <div class="stat-content">
              <div class="stat-value">{{ stats.myOrderCount }}</div>
              <div class="stat-label">我的订单</div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12">
          <el-card shadow="never" class="stat-card page-card users">
            <div class="stat-content">
              <div class="stat-value">{{ stats.myReservationCount }}</div>
              <div class="stat-label">我的预约</div>
            </div>
          </el-card>
        </el-col>
      </template>
    </el-row>

    <el-row :gutter="16" class="content-row">
      <el-col :xs="24" :lg="12">
        <el-card shadow="never" class="page-card">
          <template #header>
            <div class="cardHeader">欢迎回来</div>
          </template>
          <div class="welcome">
            <div class="welcome__title">你好，{{ username || '用户' }}</div>
            <div class="welcome__sub">欢迎使用高速服务区服务管理系统，祝您使用愉快！</div>
            <div class="welcome__time">当前时间：{{ currentTime }}</div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="12">
        <el-card shadow="never" class="page-card">
          <template #header>
            <div class="cardHeader">快速操作</div>
          </template>
          <div class="quick-actions">
            <template v-if="isPlatformAdmin">
              <el-button type="primary" @click="goTo('service-area')" class="action-btn">
                <el-icon><Location /></el-icon> 管理服务区
              </el-button>
              <el-button type="success" @click="goTo('merchant')" class="action-btn">
                <el-icon><Shop /></el-icon> 管理商户
              </el-button>
              <el-button type="warning" @click="goTo('order')" class="action-btn">
                <el-icon><Document /></el-icon> 查看订单
              </el-button>
              <el-button type="info" @click="goTo('statistics')" class="action-btn">
                <el-icon><TrendCharts /></el-icon> 查看统计
              </el-button>
              <el-button @click="goTo('order-statistics')" class="action-btn">订单统计</el-button>
            </template>
            <template v-else-if="isMerchant">
              <el-button type="primary" @click="goTo('product')" class="action-btn">商品管理</el-button>
              <el-button type="warning" @click="goTo('order')" class="action-btn">店铺订单</el-button>
              <el-button type="success" @click="goTo('reservation-manage')" class="action-btn">预约受理</el-button>
            </template>
            <template v-else>
              <el-button type="primary" @click="goTo('book')" class="action-btn">我要预约</el-button>
              <el-button type="success" @click="goTo('my-reservations')" class="action-btn">我的预约</el-button>
              <el-button type="warning" @click="goTo('order')" class="action-btn">我的订单</el-button>
              <el-button type="info" @click="goTo('nearby')" class="action-btn">附近服务区</el-button>
            </template>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="content-row">
      <el-col :xs="24">
        <el-card shadow="never" class="page-card">
          <template #header>
            <div class="cardHeader">登录信息</div>
          </template>
          <el-descriptions :column="3" border>
            <el-descriptions-item label="用户角色">{{ roleLabel || '-' }}</el-descriptions-item>
            <el-descriptions-item label="登录状态">{{ isLoggedIn ? '在线' : '离线' }}</el-descriptions-item>
            <el-descriptions-item label="用户名">{{ username || '-' }}</el-descriptions-item>
            <el-descriptions-item label="JWT 存储位置">localStorage.token</el-descriptions-item>
            <el-descriptions-item label="系统版本">v1.0.0</el-descriptions-item>
            <el-descriptions-item label="最后登录">{{ lastLoginTime || '-' }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { computed, ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Location, Shop, Document, User, TrendCharts } from '@element-plus/icons-vue'
import { useUserStore } from '../store/useUserStore'
import request from '../api/request'

const router = useRouter()
const userStore = useUserStore()

const isLoggedIn = computed(() => userStore.isLoggedIn)
const username = computed(() => userStore.username)

const roleLabel = computed(() => {
  const r = userStore.role
  const map = {
    SUPER_ADMIN: '超级管理员',
    OPERATOR: '运营管理员',
    MERCHANT: '商户',
    USER: '普通用户',
  }
  return map[r] || r
})

const isPlatformAdmin = computed(() => ['SUPER_ADMIN', 'OPERATOR'].includes(userStore.role))
const isMerchant = computed(() => userStore.role === 'MERCHANT')

const stats = ref({
  serviceAreaCount: 0,
  merchantCount: 0,
  orderCount: 0,
  userCount: 0,
  myOrderCount: 0,
  myReservationCount: 0,
  pendingOrderCount: 0,
})

const currentTime = ref('')
const timer = ref(null)
const lastLoginTime = ref('')

function updateTime() {
  const now = new Date()
  currentTime.value = now.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

function goTo(path) {
  router.push('/' + path)
}

async function fetchStats() {
  try {
    const url = isPlatformAdmin.value ? '/statistics/overview' : '/statistics/my-overview'
    const res = await request.get(url)
    const data = res.data || {}
    Object.assign(stats.value, {
      serviceAreaCount: data.serviceAreaCount || 0,
      merchantCount: data.merchantCount || 0,
      orderCount: data.orderCount || 0,
      userCount: data.userCount || 0,
      myOrderCount: data.myOrderCount || 0,
      myReservationCount: data.myReservationCount || 0,
      pendingOrderCount: data.pendingOrderCount || 0,
    })
  } catch (error) {
    const msg = error?.response?.data?.message
    console.error('加载统计数据失败', msg || error)
  }
}

onMounted(() => {
  updateTime()
  timer.value = setInterval(updateTime, 1000)
  fetchStats()
  
  // 模拟最后登录时间
  lastLoginTime.value = new Date().toLocaleString('zh-CN')
})

onUnmounted(() => {
  if (timer.value) {
    clearInterval(timer.value)
  }
})
</script>

<style scoped>
.dashboard {
  padding: 0;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  overflow: hidden;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.1);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  color: white;
}

.service-areas .stat-icon {
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
}

.merchants .stat-icon {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.orders .stat-icon {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
}

.users .stat-icon {
  background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%);
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #0f172a;
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  color: #64748b;
  margin-top: 4px;
}

.content-row {
  margin-bottom: 20px;
}

.cardHeader {
  font-weight: 600;
  color: #0f172a;
  font-size: 16px;
}

.welcome {
  padding: 8px 0;
}

.welcome__title {
  font-weight: 700;
  color: #0f172a;
  font-size: 18px;
}

.welcome__sub {
  margin-top: 8px;
  color: #64748b;
  line-height: 1.6;
}

.welcome__time {
  margin-top: 12px;
  color: #3b82f6;
  font-weight: 500;
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.action-btn {
  height: 48px;
  font-size: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  border-radius: 10px;
  transition: all 0.3s ease;
}

.action-btn:hover {
  transform: translateY(-2px);
}
</style>
