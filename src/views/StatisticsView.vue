<template>
  <div class="page-view">
    <el-card shadow="never" class="page-card">
      <template #header>
        <div class="card-header">
          <span>数据统计看板</span>
        </div>
      </template>

      <el-row :gutter="20" class="stats-row">
        <el-col :span="8">
          <div class="stat-card">
            <div class="stat-icon service-area">
              <el-icon><Location /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalServiceAreas }}</div>
              <div class="stat-label">服务区总数</div>
            </div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-card">
            <div class="stat-icon facilities">
              <el-icon><Tools /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalFacilities }}</div>
              <div class="stat-label">设施总数</div>
            </div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-card">
            <div class="stat-icon regions">
              <el-icon><MapLocation /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalRegions }}</div>
              <div class="stat-label">覆盖地区数</div>
            </div>
          </div>
        </el-col>
      </el-row>

      <el-row :gutter="20" class="chart-row">
        <el-col :span="8">
          <el-card shadow="never" class="page-card">
            <template #header><span class="page-title-inline">服务区区域分布</span></template>
            <div ref="regionChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="never" class="page-card">
            <template #header><span class="page-title-inline">设施分布</span></template>
            <div ref="facilitiesChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="never" class="page-card">
            <template #header><span class="page-title-inline">访问量排行</span></template>
            <div ref="visitsChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, nextTick } from 'vue'
import { Location, Tools, MapLocation } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getRegionStatistics, getFacilitiesStatistics, getVisitsStatistics } from '../api/statistics'

const statistics = reactive({
  totalServiceAreas: 0,
  totalFacilities: 0,
  totalRegions: 0
})

const regionChartRef = ref(null)
const facilitiesChartRef = ref(null)
const visitsChartRef = ref(null)

let regionChart = null
let facilitiesChart = null
let visitsChart = null

const loadData = async () => {
  try {
    console.log('正在加载统计数据...')
    
    const [rawRegion, rawFacilities, rawVisits] = await Promise.all([
      getRegionStatistics(),
      getFacilitiesStatistics(),
      getVisitsStatistics()
    ])

    // 提取 ApiResponse 中的实际数据
    const regionData = rawRegion?.data ?? rawRegion
    const facilitiesData = rawFacilities?.data ?? rawFacilities
    const visitsData = rawVisits?.data ?? rawVisits

    console.log('区域分布:', regionData)
    console.log('设施分布:', facilitiesData)
    console.log('访问量:', visitsData)

    // 处理区域数据
    if (regionData && Array.isArray(regionData)) {
      statistics.totalServiceAreas = regionData.reduce((sum, item) => sum + (item.count || 0), 0)
      statistics.totalRegions = regionData.length
      renderRegionChart(regionData)
    }

    // 处理设施数据
    if (facilitiesData && typeof facilitiesData === 'object') {
      const values = Object.values(facilitiesData)
      statistics.totalFacilities = values.reduce((sum, val) => sum + (Number(val) || 0), 0)
      renderFacilitiesChart(facilitiesData)
    }

    // 处理访问量数据
    if (visitsData && Array.isArray(visitsData)) {
      renderVisitsChart(visitsData.slice(0, 10))
    }
  } catch (error) {
    console.error('加载统计数据失败', error)
  }
}

const renderRegionChart = (data) => {
  if (!regionChartRef.value) return

  const el = regionChartRef.value
  const rect = el.getBoundingClientRect()
  console.log('区域图表容器尺寸:', rect.width + 'x' + rect.height, 'client:', el.clientWidth + 'x' + el.clientHeight)

  if (regionChart) {
    regionChart.dispose()
  }

  regionChart = echarts.init(el)

  const sortedData = data.sort((a, b) => b.count - a.count).slice(0, 8)

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: '5%',
      top: 'center'
    },
    series: [{
      name: '服务区数量',
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['35%', '50%'],
      avoidLabelOverlap: true,
      itemStyle: {
        borderRadius: 6,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: false
      },
      data: sortedData.map((item, index) => ({
        name: item.region || '未分配',
        value: item.count,
        itemStyle: { color: ['#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de', '#3ba272', '#fc8452', '#9a60b4'][index % 8] }
      }))
    }]
  }

  regionChart.setOption(option)
  regionChart.resize()
  const canvas = el.querySelector('canvas')
  console.log('区域图表 canvas:', canvas ? canvas.width + 'x' + canvas.height + ' visible:' + (canvas.offsetParent !== null) : '无 canvas')
  console.log('区域图表已渲染')
}

const renderFacilitiesChart = (data) => {
  if (!facilitiesChartRef.value) return

  const el = facilitiesChartRef.value
  const rect = el.getBoundingClientRect()
  console.log('设施图表容器尺寸:', rect.width + 'x' + rect.height, 'client:', el.clientWidth + 'x' + el.clientHeight)

  if (facilitiesChart) {
    facilitiesChart.dispose()
  }

  facilitiesChart = echarts.init(el)

  const sortedData = Object.entries(data)
    .map(([name, value]) => ({ name, value: Number(value) || 0 }))
    .sort((a, b) => b.value - a.value)
    .slice(0, 8)

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      formatter: '{b}: {c}'
    },
    grid: {
      left: '3%',
      right: '8%',
      bottom: '3%',
      top: '10px',
      containLabel: true
    },
    xAxis: {
      type: 'value'
    },
    yAxis: {
      type: 'category',
      data: sortedData.map(item => item.name)
    },
    series: [{
      name: '设施数量',
      type: 'bar',
      data: sortedData.map((item, index) => ({
        value: item.value,
        itemStyle: { color: ['#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de', '#3ba272', '#fc8452', '#9a60b4'][index % 8], borderRadius: [0, 4, 4, 0] }
      })),
      barWidth: '50%',
      label: {
        show: true,
        position: 'right'
      }
    }]
  }

  facilitiesChart.setOption(option)
  facilitiesChart.resize()
  const canvas2 = el.querySelector('canvas')
  console.log('设施图表 canvas:', canvas2 ? canvas2.width + 'x' + canvas2.height + ' visible:' + (canvas2.offsetParent !== null) : '无 canvas')
  console.log('设施图表已渲染')
}

const renderVisitsChart = (data) => {
  if (!visitsChartRef.value) return

  const el = visitsChartRef.value
  const rect = el.getBoundingClientRect()
  console.log('访问量图表容器尺寸:', rect.width + 'x' + rect.height, 'client:', el.clientWidth + 'x' + el.clientHeight)

  if (visitsChart) {
    visitsChart.dispose()
  }

  visitsChart = echarts.init(el)

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      formatter: '{b}: {c} 次'
    },
    grid: {
      left: '3%',
      right: '8%',
      bottom: '3%',
      top: '10px',
      containLabel: true
    },
    xAxis: {
      type: 'value'
    },
    yAxis: {
      type: 'category',
      data: data.map(item => item.name).reverse()
    },
    series: [{
      name: '访问量',
      type: 'bar',
      data: data.map(item => item.visits).reverse(),
      itemStyle: {
        color: '#5470c6',
        borderRadius: [0, 4, 4, 0]
      },
      barWidth: '60%',
      label: {
        show: true,
        position: 'right'
      }
    }]
  }

  visitsChart.setOption(option)
  visitsChart.resize()
  const canvas3 = el.querySelector('canvas')
  console.log('访问量图表 canvas:', canvas3 ? canvas3.width + 'x' + canvas3.height + ' visible:' + (canvas3.offsetParent !== null) : '无 canvas')
  console.log('访问量图表已渲染')
}

const handleResize = () => {
  regionChart?.resize()
  facilitiesChart?.resize()
  visitsChart?.resize()
}

onMounted(async () => {
  await nextTick()
  await new Promise(resolve => setTimeout(resolve, 500))
  await loadData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  regionChart?.dispose()
  facilitiesChart?.dispose()
  visitsChart?.dispose()
  window.removeEventListener('resize', handleResize)
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

.stat-icon.service-area {
  background: linear-gradient(135deg, #5470c6 0%, #3d5ab8 100%);
}

.stat-icon.facilities {
  background: linear-gradient(135deg, #91cc75 0%, #74b460 100%);
}

.stat-icon.regions {
  background: linear-gradient(135deg, #fac858 0%, #e5ab32 100%);
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

.chart-row {
  margin-top: 20px;
}

.chart-container {
  height: 280px;
  width: 100%;
}
</style>
