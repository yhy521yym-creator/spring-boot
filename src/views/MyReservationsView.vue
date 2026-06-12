<template>
  <div class="page-view">
    <el-card shadow="never" class="page-card">
      <template #header>
        <div class="card-header">
          <span>我的预约</span>
          <el-select v-model="filterStatus" placeholder="状态" clearable style="width: 140px" @change="loadList">
            <el-option label="待处理" value="PENDING" />
            <el-option label="已确认" value="CONFIRMED" />
            <el-option label="已完成" value="COMPLETED" />
            <el-option label="已取消" value="CANCELLED" />
          </el-select>
        </div>
      </template>

      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="reservationDate" label="预约日期" width="120" />
        <el-table-column prop="reservationTime" label="时间" width="100">
          <template #default="{ row }">{{ row.reservationTime || '-' }}</template>
        </el-table-column>
        <el-table-column prop="quantity" label="人数" width="70" align="center" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag size="small" :type="statusTag(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column prop="createTime" label="提交时间" width="170" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="goOrder">查看订单</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!loading && list.length === 0" description="暂无预约记录" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { reservationApi } from '../api/reservation'

const router = useRouter()
const list = ref([])
const loading = ref(false)
const filterStatus = ref('')

const statusLabel = (s) => ({
  PENDING: '待处理',
  CONFIRMED: '已确认',
  COMPLETED: '已完成',
  CANCELLED: '已取消',
}[s] || s)

const statusTag = (s) => ({
  PENDING: 'warning',
  CONFIRMED: 'primary',
  COMPLETED: 'success',
  CANCELLED: 'info',
}[s] || '')

async function loadList() {
  loading.value = true
  try {
    const params = { current: 1, size: 100 }
    if (filterStatus.value) params.status = filterStatus.value
    const res = await reservationApi.getPage(params)
    if (res.code === 0) {
      list.value = res.data?.records || res.data || []
    }
  } catch {
    ElMessage.error('加载预约失败')
  } finally {
    loading.value = false
  }
}

function goOrder() {
  router.push('/order')
}

onMounted(loadList)
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
