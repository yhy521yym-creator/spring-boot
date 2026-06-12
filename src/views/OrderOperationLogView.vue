<template>
  <div class="page-view">
    <el-card shadow="never" class="page-card">
      <template #header>
        <div class="card-header">
          <span>订单操作日志</span>
          <div class="filters">
            <el-input
              v-model="filters.orderNo"
              placeholder="订单号"
              clearable
              style="width: 200px"
              @change="loadLogs"
            />
            <el-button type="primary" @click="loadLogs">查询</el-button>
          </div>
        </div>
      </template>

      <el-table :data="logList" v-loading="loading" stripe>
        <el-table-column prop="orderNo" label="订单号" width="200" />
        <el-table-column prop="oldStatus" label="原状态" width="100">
          <template #default="{ row }">{{ statusName(row.oldStatus) }}</template>
        </el-table-column>
        <el-table-column prop="newStatus" label="新状态" width="100">
          <template #default="{ row }">
            <el-tag size="small">{{ statusName(row.newStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="operatorUsername" label="操作人" width="120" />
        <el-table-column prop="operatorRole" label="角色" width="110">
          <template #default="{ row }">{{ roleName(row.operatorRole) }}</template>
        </el-table-column>
        <el-table-column prop="remark" label="说明" min-width="140" show-overflow-tooltip />
        <el-table-column prop="createTime" label="操作时间" width="170" />
      </el-table>

      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        style="margin-top: 16px; justify-content: flex-end"
        @current-change="loadLogs"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { orderLogApi } from '../api/orderLog'

const logList = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const filters = reactive({ orderNo: '' })

const statusName = (s) => ({
  PENDING: '待确认',
  CONFIRMED: '已确认',
  COMPLETED: '已完成',
  CANCELLED: '已取消',
  PAID: '已支付',
}[s] || s || '-')

const roleName = (r) => ({
  SUPER_ADMIN: '超级管理员',
  OPERATOR: '运营',
  MERCHANT: '商户',
  USER: '用户',
}[r] || r)

async function loadLogs() {
  loading.value = true
  try {
    const params = {
      current: currentPage.value,
      size: pageSize.value,
    }
    if (filters.orderNo) params.orderNo = filters.orderNo
    const res = await orderLogApi.getPage(params)
    if (res.code === 0) {
      logList.value = res.data?.records || []
      total.value = res.data?.total || 0
    }
  } catch {
    ElMessage.error('加载日志失败')
  } finally {
    loading.value = false
  }
}

onMounted(loadLogs)
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}
.filters {
  display: flex;
  gap: 8px;
}
</style>
