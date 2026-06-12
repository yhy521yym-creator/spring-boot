<template>
  <div class="page-view">
    <el-card shadow="never" class="page-card">
      <template #header>
        <div class="card-header">
          <span>订单统计</span>
          <el-select v-model="timeRange" @change="loadData">
            <el-option label="近7天" :value="7" />
            <el-option label="近30天" :value="30" />
          </el-select>
        </div>
      </template>

      <el-row :gutter="20" class="stats-row">
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon total">
              <el-icon><ShoppingCart /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ orderStats.totalOrders || 0 }}</div>
              <div class="stat-label">订单总数</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon today">
              <el-icon><Calendar /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ orderStats.todayOrders || 0 }}</div>
              <div class="stat-label">今日订单</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon amount">
              <el-icon><Wallet /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">¥{{ (orderStats.totalAmount || 0).toFixed(2) }}</div>
              <div class="stat-label">总销售额</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon pending">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ orderStats.pendingOrders || 0 }}</div>
              <div class="stat-label">待确认订单</div>
            </div>
          </div>
        </el-col>
      </el-row>

      <el-row :gutter="20" class="stats-row">
        <el-col :span="12">
          <el-card shadow="never" class="page-card">
            <template #header><span class="page-title-inline">订单状态分布</span></template>
            <div ref="statusChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="never" class="page-card">
            <template #header><span class="page-title-inline">订单趋势</span></template>
            <div ref="trendChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-card shadow="never" class="page-card">
            <template #header><span class="page-title-inline">商户订单排行</span></template>
            <el-table :data="merchantStats" size="small" stripe>
              <el-table-column prop="merchantName" label="商户名称" />
              <el-table-column prop="orderCount" label="订单数" />
              <el-table-column prop="totalAmount" label="销售额">
                <template #default="{ row }">¥{{ (row.totalAmount || 0).toFixed(2) }}</template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="never" class="page-card">
            <template #header><span class="page-title-inline">服务区订单排行</span></template>
            <el-table :data="serviceAreaStats" size="small" stripe>
              <el-table-column prop="serviceAreaName" label="服务区" />
              <el-table-column prop="orderCount" label="订单数" />
              <el-table-column prop="totalAmount" label="销售额">
                <template #default="{ row }">¥{{ (row.totalAmount || 0).toFixed(2) }}</template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ShoppingCart, Calendar, Wallet, Clock } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { statisticsApi } from '../api/statistics'

const timeRange = ref(7)
const orderStats = reactive({
  totalOrders: 0,
  todayOrders: 0,
  totalAmount: 0,
  pendingOrders: 0,
  confirmedOrders: 0,
  completedOrders: 0,
  cancelledOrders: 0
})

const merchantStats = ref([])
const serviceAreaStats = ref([])

let statusChart = null
let trendChart = null
const statusChartRef = ref(null)
const trendChartRef = ref(null)

/** 兼容 ApiResponse：axios 已返回 response.data */
function unwrap(res) {
  if (res && typeof res === 'object' && res.code === 0) return res.data
  if (res && typeof res === 'object' && 'code' in res) return null
  return res
}

const loadData = async () => {
  await Promise.all([
    loadOrderStats(),
    loadOrderTrend(),
    loadMerchantStats(),
    loadServiceAreaStats()
  ])
  await new Promise(resolve => setTimeout(resolve, 100))
  renderStatusChart()
}

const loadOrderStats = async () => {
  try {
    const res = await statisticsApi.getOrderStatistics()
    const data = unwrap(res)
    if (data && typeof data === 'object') {
      Object.assign(orderStats, data)
    }
  } catch (error) {
    console.error('加载订单统计失败', error)
  }
}

const loadOrderTrend = async () => {
  try {
    const res = await statisticsApi.getOrderTrend(timeRange.value)
    const data = unwrap(res)
    if (Array.isArray(data)) {
      renderTrendChart(data)
    }
  } catch (error) {
    console.error('加载订单趋势失败', error)
  }
}

const loadMerchantStats = async () => {
  try {
    const res = await statisticsApi.getOrderStatisticsByMerchant()
    const data = unwrap(res)
    if (Array.isArray(data)) {
      merchantStats.value = data
        .filter((row) => Number(row.orderCount) > 0)
        .slice(0, 10)
    }
  } catch (error) {
    console.error('加载商户统计失败', error)
  }
}

const loadServiceAreaStats = async () => {
  try {
    const res = await statisticsApi.getOrderStatisticsByServiceArea()
    const data = unwrap(res)
    if (Array.isArray(data)) {
      serviceAreaStats.value = data
        .filter((row) => Number(row.orderCount) > 0)
        .slice(0, 10)
    }
  } catch (error) {
    console.error('加载服务区统计失败', error)
  }
}

const renderStatusChart = () => {
  if (!statusChartRef.value) return

  if (statusChart) {
    statusChart.dispose()
  }

  statusChart = echarts.init(statusChartRef.value)

  const data = [
    { name: '待确认', value: orderStats.pendingOrders || 0, itemStyle: { color: '#e6a23c' } },
    { name: '已确认', value: orderStats.confirmedOrders || 0, itemStyle: { color: '#67c23a' } },
    { name: '已完成', value: orderStats.completedOrders || 0, itemStyle: { color: '#409eff' } },
    { name: '已取消', value: orderStats.cancelledOrders || 0, itemStyle: { color: '#f56c6c' } },
  ].filter((item) => item.value > 0)

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      top: 'center'
    },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['60%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: false
      },
      data: data.length ? data : [{ name: '暂无订单', value: 1, itemStyle: { color: '#dcdfe6' } }],
    }],
  }

  statusChart.setOption(option)
}

const renderTrendChart = (data) => {
  if (!trendChartRef.value || !data) return

  if (trendChart) {
    trendChart.dispose()
  }

  trendChart = echarts.init(trendChartRef.value)

  const dates = data.map(d => d.date)
  const orderCounts = data.map(d => d.orderCount)
  const amounts = data.map(d => d.totalAmount)

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross'
      }
    },
    legend: {
      data: ['订单数', '销售额']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: dates
    },
    yAxis: [
      {
        type: 'value',
        name: '订单数',
        position: 'left'
      },
      {
        type: 'value',
        name: '销售额',
        position: 'right'
      }
    ],
    series: [
      {
        name: '订单数',
        type: 'line',
        smooth: true,
        data: orderCounts,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
          ])
        }
      },
      {
        name: '销售额',
        type: 'line',
        yAxisIndex: 1,
        smooth: true,
        data: amounts,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(103, 194, 58, 0.3)' },
            { offset: 1, color: 'rgba(103, 194, 58, 0.05)' }
          ])
        }
      }
    ]
  }

  trendChart.setOption(option)
}

const handleResize = () => {
  statusChart?.resize()
  trendChart?.resize()
}

onMounted(async () => {
  await new Promise(resolve => setTimeout(resolve, 300))
  await loadData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  statusChart?.dispose()
  trendChart?.dispose()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
  background: linear-gradient(135deg, #fff 0%, #f8f9fa 100%);
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  font-size: 24px;
  color: #fff;
}

.stat-icon.total {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.today {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.amount {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-icon.pending {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}

</style>
