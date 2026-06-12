<template>
  <div class="header">
    <div class="header__left">
      <div class="header__title">{{ pageTitle }}</div>
    </div>

    <div class="header__right">
      <el-tag v-if="roleLabel" type="info" effect="plain" class="header__role">{{ roleLabel }}</el-tag>

      <el-dropdown trigger="click">
        <span class="header__user">
          <el-avatar :size="28">{{ avatarText }}</el-avatar>
          <span class="header__name">{{ username || '未命名用户' }}</span>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="goUserInfo">个人中心</el-dropdown-item>
            <el-dropdown-item divided @click="onLogout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { useUserStore } from '../store/useUserStore'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const username = computed(() => userStore.username)
const role = computed(() => userStore.role)

const roleLabel = computed(() => {
  const r = role.value
  if (!r) return ''
  const map = {
    SUPER_ADMIN: '超级管理员',
    OPERATOR: '运营管理员',
    MERCHANT: '商户',
    USER: '普通用户',
  }
  return map[r] || r
})

const avatarText = computed(() => (username.value?.slice(0, 1) || 'U').toUpperCase())

const pageTitle = computed(() => route.meta?.title || '后台管理')

function goUserInfo() {
  router.push('/user')
}

async function onLogout() {
  const ok = await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '退出',
    cancelButtonText: '取消',
    type: 'warning',
  }).catch(() => false)

  if (!ok) return
  userStore.logout()
  router.replace('/login')
}
</script>

<style scoped>
.header {
  height: 60px;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(8px);
  border-bottom: 1px solid var(--color-border);
  box-shadow: 0 1px 0 rgba(15, 23, 42, 0.04);
}

.header__title {
  font-size: 17px;
  font-weight: 600;
  color: var(--color-text);
  letter-spacing: -0.02em;
}

.header__role {
  border-radius: 6px;
}

.header__right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header__user {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  user-select: none;
}

.header__name {
  color: #0f172a;
  font-size: 13px;
}
</style>