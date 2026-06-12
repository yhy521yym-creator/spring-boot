<template>
  <div class="page-view">
    <el-card shadow="never" class="page-card">
      <template #header>
        <div class="card-header">
          <span class="title">用户管理</span>
          <el-button type="primary" @click="openAddDialog">
            <el-icon><Plus /></el-icon>
            新增
          </el-button>
        </div>
      </template>

      <div class="search-bar">
        <el-form :inline="true" :model="searchForm">
          <el-form-item label="用户名">
            <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="searchForm.email" placeholder="请输入邮箱" clearable />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>
              查询
            </el-button>
            <el-button @click="resetForm">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="userList" style="width: 100%" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="username" label="用户名" align="center" />
        <el-table-column prop="email" label="邮箱" align="center" />
        <el-table-column label="角色" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="getRoleTagType(row.role)" size="small">{{ getRoleName(row.role) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="所属店铺" min-width="140" align="center">
          <template #default="{ row }">
            <span v-if="row.role === 'MERCHANT'">{{ getMerchantName(row.merchantId) }}</span>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" effect="light">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" align="center" />
        <el-table-column label="操作" min-width="220" align="center">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button type="primary" link size="small" @click="openEditDialog(row)">编辑</el-button>
              <el-button type="warning" link size="small" @click="openResetPasswordDialog(row)">重置密码</el-button>
              <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑用户' : '新增用户'"
      width="500px"
      class="user-dialog"
      destroy-on-close
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-position="right" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!isEdit">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" placeholder="请选择角色" style="width: 100%">
            <el-option label="超级管理员" value="SUPER_ADMIN" />
            <el-option label="运营管理员" value="OPERATOR" />
            <el-option label="普通用户" value="USER" />
            <el-option label="商户" value="MERCHANT" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="form.role === 'MERCHANT'" label="所属店铺" prop="merchantId">
          <el-select v-model="form.merchantId" placeholder="请选择商户店铺" style="width: 100%" filterable>
            <el-option
              v-for="m in merchantOptions"
              :key="m.id"
              :label="`${m.name}（ID:${m.id}）`"
              :value="m.id"
            />
          </el-select>
          <div class="form-tip">商户角色必须绑定店铺，否则无法管理本店商品与订单。</div>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch
            v-model="form.status"
            active-text="启用"
            inactive-text="禁用"
            :active-value="1"
            :inactive-value="0"
            :disabled="isEdit && form.role === 'SUPER_ADMIN'"
          />
          <span v-if="isEdit && form.role === 'SUPER_ADMIN'" style="color: #909399; margin-left: 10px;">
            超级管理员状态不可修改
          </span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="resetPasswordDialogVisible"
      title="重置密码"
      width="400px"
      class="user-dialog"
      destroy-on-close
    >
      <el-form :model="resetPasswordForm" :rules="resetPasswordRules" ref="resetPasswordFormRef" label-position="right" label-width="80px">
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="resetPasswordForm.newPassword" type="password" placeholder="请输入新密码" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="resetPasswordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="resetPassword">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import { userApi } from '../api/user'
import { merchantApi } from '../api/merchant'

const searchForm = reactive({
  username: '',
  email: '',
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0,
})

const userList = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const merchantOptions = ref([])

const form = reactive({
  username: '',
  password: '',
  email: '',
  role: '',
  status: 1,
  merchantId: null,
})
const formRef = ref(null)

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' },
  ],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }],
  merchantId: [{
    validator: (_r, v, cb) => {
      if (form.role === 'MERCHANT' && !v) cb(new Error('请选择所属店铺'))
      else cb()
    },
    trigger: 'change',
  }],
}

const resetPasswordDialogVisible = ref(false)
const resetPasswordForm = reactive({
  newPassword: '',
})
const resetPasswordFormRef = ref(null)
const resetPasswordRules = {
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }],
}
const currentUserId = ref(null)

const getRoleName = (role) => {
  const map = {
    SUPER_ADMIN: '超级管理员',
    OPERATOR: '运营管理员',
    USER: '普通用户',
    MERCHANT: '商户',
  }
  return map[role] || role || '-'
}

const getRoleTagType = (role) => {
  const map = { SUPER_ADMIN: 'danger', OPERATOR: 'info', USER: 'primary', MERCHANT: 'warning' }
  return map[role] || 'info'
}

const getMerchantName = (merchantId) => {
  if (!merchantId) return '未绑定'
  const m = merchantOptions.value.find((item) => Number(item.id) === Number(merchantId))
  return m ? `${m.name}（ID:${m.id}）` : `店铺ID:${merchantId}`
}

watch(
  () => form.role,
  (role) => {
    if (role !== 'MERCHANT') {
      form.merchantId = null
    }
  },
)

const getUserList = async () => {
  loading.value = true
  try {
    const res = await userApi.list({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      username: searchForm.username,
      email: searchForm.email,
    })
    if (res.code === 0) {
      userList.value = res.data.records || []
      pagination.total = res.data.total || 0
    }
  } catch (error) {
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.pageNum = 1
  getUserList()
}

const resetForm = () => {
  searchForm.username = ''
  searchForm.email = ''
  handleSearch()
}

const handleSizeChange = (size) => {
  pagination.pageSize = size
  getUserList()
}

const handleCurrentChange = (current) => {
  pagination.pageNum = current
  getUserList()
}

const loadMerchants = async () => {
  try {
    const res = await merchantApi.getPage({ current: 1, size: 500 })
    if (res.code === 0) {
      merchantOptions.value = res.data?.records || res.data?.list || []
    }
  } catch {
    merchantOptions.value = []
  }
}

const openAddDialog = () => {
  isEdit.value = false
  form.username = ''
  form.password = ''
  form.email = ''
  form.role = ''
  form.status = 1
  form.merchantId = null
  dialogVisible.value = true
}

const openEditDialog = (row) => {
  isEdit.value = true
  form.username = row.username
  form.email = row.email || ''
  form.role = row.role || ''
  form.status = row.status
  form.merchantId = row.merchantId || null
  currentUserId.value = row.id
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        let res
        if (isEdit.value) {
          res = await userApi.update(currentUserId.value, {
            username: form.username,
            email: form.email,
            role: form.role,
            status: form.status,
            merchantId: form.role === 'MERCHANT' ? form.merchantId : null,
          })
        } else {
          res = await userApi.add({
            username: form.username,
            password: form.password,
            email: form.email,
            role: form.role,
            status: form.status,
            merchantId: form.role === 'MERCHANT' ? form.merchantId : null,
          })
        }

        if (res.code === 0) {
          ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
          dialogVisible.value = false
          getUserList()
        } else {
          ElMessage.error(res.message || (isEdit.value ? '编辑失败' : '新增失败'))
        }
      } catch (error) {
        ElMessage.error(isEdit.value ? '编辑失败' : '新增失败')
      }
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除用户「${row.username}」吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      try {
        const res = await userApi.delete(row.id)
        if (res.code === 0) {
          ElMessage.success('删除成功')
          getUserList()
        } else {
          ElMessage.error(res.message || '删除失败')
        }
      } catch (error) {
        ElMessage.error('删除失败')
      }
    })
    .catch(() => {})
}

const openResetPasswordDialog = (row) => {
  currentUserId.value = row.id
  resetPasswordForm.newPassword = ''
  resetPasswordDialogVisible.value = true
}

const resetPassword = async () => {
  if (!resetPasswordFormRef.value) return

  await resetPasswordFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await userApi.resetPassword(currentUserId.value, {
          newPassword: resetPasswordForm.newPassword,
        })
        if (res.code === 0) {
          ElMessage.success('密码重置成功')
          resetPasswordDialogVisible.value = false
        } else {
          ElMessage.error(res.message || '重置失败')
        }
      } catch (error) {
        ElMessage.error('重置失败')
      }
    }
  })
}

onMounted(() => {
  loadMerchants()
  getUserList()
})
</script>

<style scoped>
.user-management {
  padding: 20px;
}

.card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header .title {
  font-size: 16px;
  font-weight: 600;
}

.search-bar {
  margin-bottom: 20px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 4px;
}

.search-bar :deep(.el-form-item) {
  margin-bottom: 0;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 8px;
}

:deep(.el-table) {
  border: 1px solid #ebeef5;
  border-radius: 4px;
}

:deep(.el-table th) {
  background-color: #fafafa !important;
}

:deep(.el-table--striped .el-table__body tr.el-table__row--striped td) {
  background: #fafafa;
}

:deep(.user-dialog) {
  border-radius: 8px;
}

:deep(.user-dialog .el-dialog__header) {
  border-bottom: 1px solid #ebeef5;
  padding: 16px 20px;
  margin-right: 0;
}

:deep(.user-dialog .el-dialog__body) {
  padding: 24px 20px;
}

:deep(.user-dialog .el-dialog__footer) {
  border-top: 1px solid #ebeef5;
  padding: 12px 20px;
}

:deep(.el-form-item__label) {
  font-weight: 500;
}

:deep(.el-dialog__wrapper) {
  backdrop-filter: blur(4px);
}

:deep(.el-button + .el-button) {
  margin-left: 12px;
}

.text-muted {
  color: #909399;
}

.form-tip {
  margin-top: 6px;
  font-size: 12px;
  color: #64748b;
  line-height: 1.4;
}
</style>
