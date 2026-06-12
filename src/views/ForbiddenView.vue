<template>
  <div class="page-view forbidden">
    <el-card shadow="never" class="page-card">
    <el-result icon="warning" title="403" sub-title="您没有权限访问该页面">
      <template #extra>
        <el-button type="primary" @click="goHome">返回首页</el-button>
      </template>
    </el-result>
    </el-card>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/useUserStore'

const router = useRouter()
const userStore = useUserStore()

function goHome() {
  router.replace('/dashboard')
}
</script>

<style scoped>
.forbidden .page-card {
  max-width: 520px;
  margin: 48px auto;
  text-align: center;
}
</style>
