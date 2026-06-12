<template>
  <div class="sidebar">
    <div class="brand">
      <div class="brand__title">服务区服务管理系统</div>
    </div>

    <el-menu
      :default-active="activePath"
      router
      class="menu"
      background-color="transparent"
      text-color="#a1a5b7"
      active-text-color="#3b82f6"
    >
      <template v-for="item in visibleMenus" :key="item.path">
        <el-sub-menu v-if="item.children?.length" :index="item.path">
          <template #title>
            <span>{{ getMenuLabel(item) }}</span>
          </template>
          <el-menu-item v-for="child in item.children" :key="child.path" :index="child.path">
            <span>{{ child.label }}</span>
          </el-menu-item>
        </el-sub-menu>
        <el-menu-item v-else :index="item.path">
          <span>{{ getMenuLabel(item) }}</span>
        </el-menu-item>
      </template>
    </el-menu>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '../store/useUserStore'
import { filterMenusByRole, getMenuLabel as resolveMenuLabel } from '../config/menus'

const route = useRoute()
const userStore = useUserStore()

const role = computed(() => userStore.role || 'USER')
const visibleMenus = computed(() => filterMenusByRole(role.value))

function getMenuLabel(item) {
  return resolveMenuLabel(item, role.value)
}

const activePath = computed(() => route.path)
</script>

<style scoped>
.sidebar {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #1e293b;
  border-right: 1px solid rgba(255, 255, 255, 0.06);
}

.brand {
  padding: 20px 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

.brand__title {
  color: #ffffff;
  font-weight: 700;
  font-size: 14px;
}

.menu {
  border-right: none;
  flex: 1;
  padding: 12px;
}

:deep(.el-menu-item) {
  padding: 11px 14px !important;
  margin: 3px 0;
  border-radius: 8px;
  height: auto;
  line-height: 1.4;
  transition: background 0.2s ease, color 0.2s ease;
}

:deep(.el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.06) !important;
}

:deep(.el-menu-item.is-active) {
  background: rgba(59, 130, 246, 0.18) !important;
  color: #93c5fd !important;
  font-weight: 500;
}

:deep(.el-sub-menu__title) {
  padding: 11px 14px !important;
  margin: 3px 0;
  border-radius: 8px;
}

:deep(.el-sub-menu .el-menu-item) {
  padding-left: 28px !important;
  min-width: auto;
}
</style>
