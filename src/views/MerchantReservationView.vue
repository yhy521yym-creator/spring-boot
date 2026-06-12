<template>
  <div class="page-view">
    <el-card shadow="never" class="page-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span>预约受理</span>
            <el-text type="info" size="small">确认/完成/取消将同步更新关联订单</el-text>
          </div>
          <el-select v-model="filterStatus" placeholder="状态" clearable style="width: 140px" @change="loadList">
            <el-option label="待处理" value="PENDING" />
            <el-option label="已确认" value="CONFIRMED" />
            <el-option label="已完成" value="COMPLETED" />
            <el-option label="已取消" value="CANCELLED" />
          </el-select>
        </div>
      </template>

      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="userName" label="用户" width="100" />
        <el-table-column prop="userPhone" label="电话" width="120" />
        <el-table-column prop="reservationDate" label="预约日期" width="120" />
        <el-table-column prop="reservationTime" label="时间" width="100" />
        <el-table-column prop="quantity" label="人数" width="70" align="center" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag size="small">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="orderId" label="关联订单" width="90" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.orderId" type="success" size="small">#{{ row.orderId }}</el-tag>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 'PENDING'" type="success" link size="small" @click="setStatus(row, 'CONFIRMED')">确认</el-button>
            <el-button v-if="row.status === 'CONFIRMED'" type="primary" link size="small" @click="setStatus(row, 'COMPLETED')">完成</el-button>
            <el-button v-if="row.status === 'PENDING' || row.status === 'CONFIRMED'" type="danger" link size="small" @click="setStatus(row, 'CANCELLED')">取消</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { reservationApi } from '../api/reservation'

const list = ref([])
const loading = ref(false)
const filterStatus = ref('')

const statusLabel = (s) => ({
  PENDING: '待处理',
  CONFIRMED: '已确认',
  COMPLETED: '已完成',
  CANCELLED: '已取消',
}[s] || s)

async function loadList() {
  loading.value = true
  try {
    const params = {}
    if (filterStatus.value) params.status = filterStatus.value
    const res = await reservationApi.getList(params)
    if (res.code === 0) {
      list.value = res.data || []
    }
  } catch {
    ElMessage.error('加载预约失败')
  } finally {
    loading.value = false
  }
}

async function setStatus(row, status) {
  try {
    const res = await reservationApi.updateStatus(row.id, status)
    if (res.code === 0) {
      ElMessage.success('操作成功')
      loadList()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch {
    ElMessage.error('操作失败')
  }
}

onMounted(loadList)
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}

.header-left {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.text-muted {
  color: #94a3b8;
  font-size: 12px;
}
</style>
