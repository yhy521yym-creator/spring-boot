<template>
  <div class="page-view">
  <el-card shadow="never" class="page-card">
    <template #header>
      <div class="cardHeader">系统设置（示例页）</div>
    </template>

    <el-alert
      title="该页面为占位示例：仅 SUPER_ADMIN 可在侧边栏看到。"
      type="info"
      show-icon
      :closable="false"
    />

    <div class="content">
      后续你可以把“用户管理 / 角色权限 / 站点配置”等真实功能页面挂到这里。
    </div>
  </el-card>
  </div>
</template>

<script setup></script>

<style scoped>
.cardHeader {
  font-weight: 600;
  color: #0f172a;
}

.content {
  margin-top: 12px;
  color: #334155;
  line-height: 1.7;
}
</style>

