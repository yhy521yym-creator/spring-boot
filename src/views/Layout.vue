<template>
  <el-container class="layout">
    <el-aside width="240px" class="layout__aside">
      <Sidebar />
    </el-aside>

    <el-container>
      <el-header class="layout__header">
        <Header />
      </el-header>

      <el-main class="layout__main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import Sidebar from '../components/Sidebar.vue'
import Header from '../components/Header.vue'
</script>

<style scoped>
.layout {
  height: 100vh;
  background: var(--color-bg-page);
}

.layout__aside {
  background: #0f172a;
  box-shadow: 2px 0 12px rgba(15, 23, 42, 0.08);
}

.layout__header {
  padding: 0;
  height: 60px;
}

.layout__main {
  padding: 20px 24px 28px;
  background: linear-gradient(165deg, #eef2f7 0%, #e8eef5 40%, #f4f6f9 100%);
  overflow-x: hidden;
  overflow-y: auto;
}
</style>

