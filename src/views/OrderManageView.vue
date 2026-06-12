<template>
  <div class="page-view">
    <el-card shadow="never" class="page-card">
      <template #header>
        <div class="card-header">
          <span>{{ pageTitle }}</span>
          <div v-if="isAdmin" class="filter-row">
            <el-select v-model="filters.serviceAreaId" placeholder="选择服务区" clearable style="width: 150px;" @change="handleFilterChange">
              <el-option v-for="item in serviceAreaList" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
            <el-select v-model="filters.status" placeholder="订单状态" clearable style="width: 120px;" @change="handleFilterChange">
              <el-option label="待确认" value="PENDING" />
              <el-option label="已确认" value="CONFIRMED" />
              <el-option label="已完成" value="COMPLETED" />
              <el-option label="已取消" value="CANCELLED" />
            </el-select>
            <el-date-picker
              v-model="filters.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD"
              style="width: 240px;"
              @change="handleFilterChange"
            />
            <el-input v-model="filters.keyword" placeholder="订单号/用户名" style="width: 150px;" clearable @change="handleFilterChange">
              <template #prefix><el-icon><Search /></el-icon></template>
            </el-input>
          </div>
        </div>
      </template>

      <el-table :data="orderList" style="width: 100%" v-loading="loading" stripe>
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="serviceAreaName" label="服务区" width="140" />
        <el-table-column prop="productName" label="商品" width="120" />
        <el-table-column prop="merchantName" label="商户" width="120" />
        <el-table-column prop="userName" label="用户" width="100" />
        <el-table-column prop="quantity" label="数量" width="70" align="center" />
        <el-table-column prop="totalPrice" label="应付金额" width="100">
          <template #default="{ row }">¥{{ formatMoney(row.totalPrice) }}</template>
        </el-table-column>
        <el-table-column prop="status" label="订单状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)" size="small">{{ getStatusName(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="支付" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="payStatusTagType(row)" size="small">{{ payStatusLabel(row) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reservationDate" label="预约日期" width="120" />
        <el-table-column prop="reservationTime" label="预约时间" width="100" />
        <el-table-column prop="createTime" label="下单时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="showDetail(row)">详情</el-button>
            <el-button
              type="warning"
              link
              size="small"
              v-if="isEndUser && canPayOrder(row)"
              @click="openPay(row)"
            >
              去支付
            </el-button>
            <el-button
              type="success"
              link
              size="small"
              @click="handleConfirm(row)"
              v-if="canManageOrder && row.status === 'PENDING' && resolvePayStatus(row) === 'PAID'"
            >
              确认
            </el-button>
            <el-button type="warning" link size="small" @click="handleComplete(row)" v-if="canManageOrder && row.status === 'CONFIRMED'">完成</el-button>
            <el-button type="danger" link size="small" @click="handleCancel(row)" v-if="(canManageOrder && (row.status === 'PENDING' || row.status === 'CONFIRMED')) || (isEndUser && (row.status === 'PENDING' || row.status === 'CONFIRMED'))">取消</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        style="margin-top: 16px; justify-content: flex-end;"
        @size-change="loadOrders"
        @current-change="loadOrders"
      />
    </el-card>
  </div>

  <el-dialog v-model="detailVisible" title="订单详情" width="600px" destroy-on-close>
    <el-descriptions :column="2" border v-if="currentOrder">
      <el-descriptions-item label="订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
      <el-descriptions-item label="订单状态">
        <el-tag :type="getStatusTagType(currentOrder.status)" size="small">{{ getStatusName(currentOrder.status) }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="支付状态">
        <el-tag :type="payStatusTagType(currentOrder)" size="small">{{ payStatusLabel(currentOrder) }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item v-if="currentOrder.payTime" label="支付时间">{{ currentOrder.payTime }}</el-descriptions-item>
      <el-descriptions-item v-if="currentOrder.payMethod" label="支付方式">{{ getPayMethodName(currentOrder.payMethod) }}</el-descriptions-item>
      <el-descriptions-item label="服务区">{{ currentOrder.serviceAreaName }}</el-descriptions-item>
      <el-descriptions-item label="商户">{{ currentOrder.merchantName }}</el-descriptions-item>
      <el-descriptions-item label="商品">{{ currentOrder.productName }}</el-descriptions-item>
      <el-descriptions-item label="用户">{{ currentOrder.userName }}</el-descriptions-item>
      <el-descriptions-item label="数量">{{ currentOrder.quantity }}</el-descriptions-item>
      <el-descriptions-item label="总价">¥{{ currentOrder.totalPrice }}</el-descriptions-item>
      <el-descriptions-item label="预约日期">{{ currentOrder.reservationDate }}</el-descriptions-item>
      <el-descriptions-item label="预约时间">{{ currentOrder.reservationTime || '-' }}</el-descriptions-item>
      <el-descriptions-item label="联系人">{{ currentOrder.contactName }}</el-descriptions-item>
      <el-descriptions-item label="联系电话">{{ currentOrder.contactPhone }}</el-descriptions-item>
      <el-descriptions-item label="下单时间" :span="2">{{ currentOrder.createTime }}</el-descriptions-item>
      <el-descriptions-item label="备注" :span="2">{{ currentOrder.remark || '-' }}</el-descriptions-item>
    </el-descriptions>

    <div v-if="orderLogs.length" class="log-section">
      <div class="log-title">状态变更记录</div>
      <el-timeline>
        <el-timeline-item
          v-for="log in orderLogs"
          :key="log.id"
          :timestamp="log.createTime"
          placement="top"
        >
          <strong>{{ log.operatorUsername }}</strong>
          （{{ roleLabel(log.operatorRole) }}）
          ：{{ getStatusName(log.oldStatus) }} → {{ getStatusName(log.newStatus) }}
          <span v-if="log.remark" class="log-remark"> — {{ log.remark }}</span>
        </el-timeline-item>
      </el-timeline>
    </div>

    <template #footer v-if="currentOrder">
      <el-button @click="detailVisible = false">关闭</el-button>
      <el-button type="warning" v-if="isEndUser && canPayOrder(currentOrder)" @click="openPay(currentOrder)">去支付</el-button>
      <el-button
        type="success"
        @click="handleConfirm(currentOrder)"
        v-if="canManageOrder && currentOrder.status === 'PENDING' && resolvePayStatus(currentOrder) === 'PAID'"
      >
        确认订单
      </el-button>
      <el-button type="warning" @click="handleComplete(currentOrder)" v-if="canManageOrder && currentOrder.status === 'CONFIRMED'">完成订单</el-button>
      <el-button type="danger" @click="handleCancel(currentOrder)" v-if="(canManageOrder || isEndUser) && (currentOrder.status === 'PENDING' || currentOrder.status === 'CONFIRMED')">取消订单</el-button>
    </template>
  </el-dialog>

  <PayOrderDialog
    :visible="payVisible"
    :order="payOrder"
    @close="payVisible = false"
    @success="onPaySuccess"
  />
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import PayOrderDialog from '../components/PayOrderDialog.vue'
import {
  resolvePayStatus as resolveOrderPayStatus,
  payStatusLabel as getOrderPayLabel,
  payStatusTagType as getOrderPayTagType,
  canPayOrder as canUserPayOrder,
} from '../utils/orderPay'

const resolvePayStatus = (row) => resolveOrderPayStatus(row)
const payStatusLabel = (row) => getOrderPayLabel(row)
const payStatusTagType = (row) => getOrderPayTagType(row)
const canPayOrder = (row) => canUserPayOrder(row)
import { Search } from '@element-plus/icons-vue'
import { orderApi } from '../api/order'
import { orderLogApi } from '../api/orderLog'
import { productApi } from '../api/product'
import { merchantApi } from '../api/merchant'
import { getServiceAreaList } from '../api/serviceArea'
import { userApi } from '../api/user'
import { useUserStore } from '../store/useUserStore'

const orderList = ref([])
const serviceAreaList = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const detailVisible = ref(false)
const currentOrder = ref(null)
const orderLogs = ref([])

const route = useRoute()
const userStore = useUserStore()
const payVisible = ref(false)
const payOrder = ref(null)
const isAdmin = computed(() => ['SUPER_ADMIN', 'OPERATOR'].includes(userStore.role))
const isMerchant = computed(() => userStore.role === 'MERCHANT')
const isEndUser = computed(() => userStore.role === 'USER')

const pageTitle = computed(() => {
  if (isEndUser.value) return '我的订单'
  if (isMerchant.value) return '店铺订单'
  return '订单管理'
})

const canManageOrder = computed(() => isAdmin.value || isMerchant.value)

const filters = reactive({
  serviceAreaId: null,
  status: '',
  dateRange: null,
  keyword: ''
})

const getStatusName = (status) => {
  const map = {
    PENDING: '待确认',
    CONFIRMED: '已确认',
    COMPLETED: '已完成',
    CANCELLED: '已取消'
  }
  return map[status] || status
}

const getStatusTagType = (status) => {
  const map = {
    PENDING: 'warning',
    CONFIRMED: 'primary',
    COMPLETED: 'success',
    CANCELLED: 'info'
  }
  return map[status] || ''
}

const formatMoney = (val) => {
  const n = Number(val)
  return Number.isFinite(n) ? n.toFixed(2) : '0.00'
}

const getPayMethodName = (method) => {
  const map = { WECHAT: '微信支付', ALIPAY: '支付宝', SIMULATE: '模拟钱包', LEGACY: '历史订单' }
  return map[method] || method || '-'
}

const openPay = async (row) => {
  try {
    const res = await orderApi.getById(row.id)
    if (res.code === 0 && res.data) {
      payOrder.value = {
        ...row,
        ...res.data,
        productName: row.productName,
        merchantName: row.merchantName,
        serviceAreaName: row.serviceAreaName,
      }
    } else {
      payOrder.value = row
    }
  } catch {
    payOrder.value = row
  }
  if (resolvePayStatus(payOrder.value) === 'PAID') {
    ElMessage.info('该订单已支付，无需重复支付')
    return
  }
  if (!canPayOrder(payOrder.value)) {
    ElMessage.warning('当前订单不可支付，请刷新页面')
    return
  }
  payVisible.value = true
}

const onPaySuccess = () => {
  payVisible.value = false
  detailVisible.value = false
  loadOrders()
}

const handleFilterChange = () => {
  currentPage.value = 1
  loadOrders()
}

const loadServiceAreas = async () => {
  try {
    const res = await getServiceAreaList({ pageNum: 1, pageSize: 100 })
    if (res.code === 0) {
      serviceAreaList.value = res.data.records || res.data.list || []
    }
  } catch (error) {
    console.error('加载服务区列表失败', error)
  }
}

const loadOrders = async () => {
  try {
    loading.value = true
    const params = {
      current: currentPage.value,
      size: pageSize.value,
    }
    if (filters.status) {
      params.status = filters.status
    }
    if (filters.serviceAreaId) {
      params.serviceAreaId = filters.serviceAreaId
      console.log('loadOrders - serviceAreaId:', filters.serviceAreaId)
    }
    if (filters.dateRange && filters.dateRange.length === 2) {
      params.startDate = filters.dateRange[0]
      params.endDate = filters.dateRange[1]
    }
    if (filters.keyword) {
      params.keyword = filters.keyword
    }
    const res = await orderApi.getPage(params)
    if (res.code === 0) {
      const data = res.data
      orderList.value = data.records || data.list || []
      total.value = data.total || 0

      for (const item of orderList.value) {
        try {
          const pRes = await productApi.getById(item.productId)
          if (pRes.code === 0) {
            item.productName = pRes.data.name
          }
        } catch {
          item.productName = '-'
        }
        try {
          const mRes = await merchantApi.getById(item.merchantId)
          if (mRes.code === 0) {
            item.merchantName = mRes.data.name
            if (mRes.data.serviceAreaId) {
              const sa = serviceAreaList.value.find(s => Number(s.id) === Number(mRes.data.serviceAreaId))
              item.serviceAreaName = sa ? sa.name : '-'
            } else {
              item.serviceAreaName = '-'
            }
          }
        } catch {
          item.merchantName = '-'
          item.serviceAreaName = '-'
        }
        if (isEndUser.value) {
          item.userName = userStore.username || '-'
        } else {
          try {
            const uRes = await userApi.getById(item.userId)
            if (uRes.code === 0) {
              item.userName = uRes.data?.username || '-'
            }
          } catch {
            item.userName = '-'
          }
        }
      }
    }
  } catch (error) {
    ElMessage.error('加载订单失败')
  } finally {
    loading.value = false
  }
}

const roleLabel = (r) => ({
  SUPER_ADMIN: '超管',
  OPERATOR: '运营',
  MERCHANT: '商户',
  USER: '用户',
}[r] || r)

const showDetail = async (row) => {
  currentOrder.value = row
  orderLogs.value = []
  detailVisible.value = true
  try {
    const res = await orderLogApi.getByOrderId(row.id)
    if (res.code === 0) {
      orderLogs.value = res.data || []
    }
  } catch {
    orderLogs.value = []
  }
}

const handleConfirm = async (row) => {
  try {
    const res = await orderApi.updateStatus(row.id, 'CONFIRMED')
    if (res.code === 0) {
      ElMessage.success('订单已确认')
      loadOrders()
      detailVisible.value = false
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch {
    ElMessage.error('操作失败')
  }
}

const handleCancel = async (row) => {
  try {
    const res = await orderApi.updateStatus(row.id, 'CANCELLED')
    if (res.code === 0) {
      ElMessage.success('订单已取消')
      loadOrders()
      detailVisible.value = false
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch {
    ElMessage.error('操作失败')
  }
}

const handleComplete = async (row) => {
  try {
    const res = await orderApi.updateStatus(row.id, 'COMPLETED')
    if (res.code === 0) {
      ElMessage.success('订单已完成')
      loadOrders()
      detailVisible.value = false
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch {
    ElMessage.error('操作失败')
  }
}

onMounted(async () => {
  if (!isAdmin.value && !userStore.user?.merchantId) {
    await userStore.fetchMe()
  }
  loadServiceAreas()
  await loadOrders()
  const payId = route.query.payId
  if (payId && isEndUser.value) {
    const row = orderList.value.find((o) => String(o.id) === String(payId))
    if (row && canPayOrder(row)) {
      openPay(row)
    }
  }
})
</script>

<style scoped>
.card-header {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.filter-row {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.log-section {
  margin-top: 20px;
  padding-top: 12px;
  border-top: 1px solid #ebeef5;
}
.log-title {
  font-weight: 600;
  margin-bottom: 12px;
  color: #303133;
}
.log-remark {
  color: #909399;
  font-size: 13px;
}
</style>
