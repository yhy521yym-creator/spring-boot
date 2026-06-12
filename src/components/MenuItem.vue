<template>
  <template v-for="item in menus" :key="item.id">
    <!-- 有子菜单的菜单项 -->
    <el-sub-menu v-if="item.children && item.children.length > 0" :index="String(item.id)">
      <template #title>
        <el-icon v-if="item.icon">
          <component :is="item.icon" />
        </el-icon>
        <span>{{ item.name }}</span>
      </template>
      <menu-item :menus="item.children" />
    </el-sub-menu>

    <!-- 没有子菜单的菜单项 -->
    <el-menu-item v-else :index="item.path || String(item.id)" @click="handleMenuClick(item)">
      <el-icon v-if="item.icon">
        <component :is="item.icon" />
      </el-icon>
      <span>{{ item.name }}</span>
    </el-menu-item>
  </template>
</template>

<script setup>
import { useRouter } from 'vue-router'

const props = defineProps({
  menus: {
    type: Array,
    default: () => []
  }
})

const router = useRouter()

const handleMenuClick = (menu) => {
  if (menu.path) {
    router.push(menu.path)
  }
}
</script>

<style scoped>
.el-icon {
  margin-right: 8px;
}
</style>