<template>
  <div class="page-view">
    <el-card shadow="never" class="page-card">
      <template #header>
        <div class="card-header">
          <span>商户管理</span>
          <el-button type="primary" @click="handleAdd">新增商户</el-button>
        </div>
      </template>

      <el-row :gutter="20" class="filters">
        <el-col :span="6">
          <el-select v-model="filterServiceArea" placeholder="选择服务区" clearable @change="loadMerchants">
            <el-option v-for="sa in serviceAreaList" :key="sa.id" :label="sa.name" :value="sa.id" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-input v-model="filterKeyword" placeholder="商户名称/联系人" clearable @change="loadMerchants">
            <template #prefix><el-icon><Search /></el-icon></template>
          </el-input>
        </el-col>
        <el-col :span="6">
          <el-select v-model="filterStatus" placeholder="状态筛选" clearable @change="loadMerchants">
            <el-option label="全部" value="" />
            <el-option label="待审核" :value="0" />
            <el-option label="营业中" :value="1" />
            <el-option label="暂停营业" :value="2" />
            <el-option label="已关闭" :value="3" />
          </el-select>
        </el-col>
      </el-row>

      <el-table :data="merchantList" style="width: 100%; min-width: 1400px;" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column label="图片" width="80">
          <template #default="{ row }">
            <el-image
              v-if="row.imageUrl"
              :src="resolveImageUrl(row.imageUrl)"
              style="width: 48px; height: 48px; border-radius: 6px"
              fit="cover"
            />
            <span v-else class="text-gray">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="商户名称" width="120" />
        <el-table-column prop="serviceAreaName" label="所属服务区" width="140" />
        <el-table-column prop="contactName" label="联系人" width="80" />
        <el-table-column prop="contactPhone" label="联系电话" width="110" />
        <el-table-column prop="address" label="地址" width="150" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)" size="small">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="auditRemark" label="审核备注" width="180" show-overflow-tooltip>
          <template #default="{ row }">
            <span v-if="row.auditRemark && row.auditRemark !== '-'">{{ row.auditRemark }}</span>
            <span v-else class="text-gray">-</span>
          </template>
        </el-table-column>
        <el-table-column label="登录账号" width="140">
          <template #default="{ row }">
            <template v-if="row.boundAccounts?.length">
              <el-tag v-for="u in row.boundAccounts" :key="u.id" size="small" class="account-tag">
                {{ u.username }}
              </el-tag>
            </template>
            <span v-else class="text-gray">未绑定</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="success" link size="small" @click="openAccountDialog(row)">创建账号</el-button>
            <template v-if="row.status === 0">
              <el-button type="success" link size="small" @click="handleAudit(row, 1)">审核通过</el-button>
              <el-button type="danger" link size="small" @click="handleAudit(row, 3)">拒绝</el-button>
            </template>
            <template v-else-if="row.status === 1">
              <el-button type="warning" link size="small" @click="handleToggleStatus(row, 2)">暂停营业</el-button>
              <el-button type="danger" link size="small" @click="handleToggleStatus(row, 3)">关闭</el-button>
            </template>
            <template v-else-if="row.status === 2">
              <el-button type="success" link size="small" @click="handleToggleStatus(row, 1)">恢复营业</el-button>
              <el-button type="danger" link size="small" @click="handleToggleStatus(row, 3)">关闭</el-button>
            </template>
            <template v-else-if="row.status === 3">
              <el-tag type="danger" size="small">已关闭</el-tag>
              <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" destroy-on-close>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="所属服务区" prop="serviceAreaId">
          <el-select v-model="form.serviceAreaId" placeholder="选择服务区" style="width: 100%">
            <el-option v-for="sa in serviceAreaList" :key="sa.id" :label="sa.name" :value="sa.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="商户名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入商户名称" />
        </el-form-item>
        <el-form-item label="商户类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择商户类型" style="width: 100%">
            <el-option label="餐饮" value="RESTAURANT" />
            <el-option label="酒店" value="HOTEL" />
            <el-option label="充电桩" value="CHARGING" />
            <el-option label="加油站" value="GAS_STATION" />
          </el-select>
        </el-form-item>
        <el-form-item label="联系人" prop="contactName">
          <el-input v-model="form.contactName" placeholder="请输入联系人" />
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="form.address" placeholder="请输入地址" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="2" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="商户图片">
          <ImageUpload v-model="form.imageUrl" type="merchant" tip="建议 1:1，不超过 2MB" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="accountDialogVisible"
      :title="`创建商户登录账号 - ${accountMerchant?.name || ''}`"
      width="480px"
      destroy-on-close
    >
      <el-alert
        type="info"
        :closable="false"
        show-icon
        style="margin-bottom: 16px"
        title="将为该店铺创建角色为「商户」的登录用户，并自动绑定 merchant_id。"
      />
      <el-form
        v-if="accountMerchant"
        ref="accountFormRef"
        :model="accountForm"
        :rules="accountRules"
        label-width="90px"
      >
        <el-form-item label="店铺ID">
          <el-input :model-value="String(accountMerchant.id)" disabled />
        </el-form-item>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="accountForm.username" placeholder="登录用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="accountForm.password" type="password" show-password placeholder="初始密码" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="accountForm.email" placeholder="选填，用于联系" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="accountForm.phone" placeholder="选填" />
        </el-form-item>
      </el-form>
      <div v-if="accountMerchant?.boundAccounts?.length" class="bound-list">
        <div class="bound-list__title">已绑定账号</div>
        <el-tag
          v-for="u in accountMerchant.boundAccounts"
          :key="u.id"
          type="success"
          size="small"
          class="account-tag"
        >
          {{ u.username }}
        </el-tag>
      </div>
      <template #footer>
        <el-button @click="accountDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="accountSubmitting" @click="submitAccount">创建并绑定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="auditDialogVisible" title="审核商户" width="400px" destroy-on-close>
      <el-form :model="auditForm" label-width="80px">
        <el-form-item label="审核备注">
          <el-input v-model="auditForm.remark" type="textarea" :rows="3" placeholder="请输入审核备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="auditDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmAudit(3)">拒绝</el-button>
        <el-button type="primary" @click="confirmAudit(1)">审核通过</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { merchantApi } from '../api/merchant'
import { getServiceAreaList } from '../api/serviceArea'
import ImageUpload from '../components/ImageUpload.vue'
import { resolveImageUrl } from '../utils/image'

const merchantList = ref([])
const serviceAreaList = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const auditDialogVisible = ref(false)
const dialogTitle = ref('新增商户')
const formRef = ref(null)
const isEdit = ref(false)

const filterServiceArea = ref('')
const filterKeyword = ref('')
const filterStatus = ref('')

const currentAuditRow = ref(null)
const targetAuditStatus = ref(1)

const form = reactive({
  id: null,
  serviceAreaId: null,
  name: '',
  type: '',
  contactName: '',
  contactPhone: '',
  address: '',
  description: '',
  imageUrl: '',
})

const auditForm = reactive({
  remark: ''
})

const accountDialogVisible = ref(false)
const accountMerchant = ref(null)
const accountSubmitting = ref(false)
const accountFormRef = ref(null)
const accountForm = reactive({
  username: '',
  password: '',
  email: '',
  phone: '',
})
const accountRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少 6 位', trigger: 'blur' },
  ],
  email: [{ type: 'email', message: '邮箱格式不正确', trigger: 'blur' }],
}

const rules = {
  serviceAreaId: [{ required: true, message: '请选择服务区', trigger: 'change' }],
  name: [{ required: true, message: '请输入商户名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择商户类型', trigger: 'change' }],
  contactName: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
  contactPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }]
}

const statusConfig = {
  0: { text: '待审核', type: 'warning' },
  1: { text: '营业中', type: 'success' },
  2: { text: '暂停营业', type: 'info' },
  3: { text: '已关闭', type: 'danger' }
}

const getStatusText = (status) => {
  return statusConfig[status]?.text || '未知'
}

const getStatusTagType = (status) => {
  return statusConfig[status]?.type || 'default'
}

const loadServiceAreas = async () => {
  try {
    const res = await getServiceAreaList({ pageNum: 1, pageSize: 100 })
    if (res.code === 0) {
      serviceAreaList.value = res.data.records || res.data.list || []
    }
  } catch (error) {
    console.error('加载服务区失败', error)
  }
}

const loadMerchants = async () => {
  try {
    loading.value = true
    const params = { size: 1000 }
    if (filterServiceArea.value && filterServiceArea.value !== '') params.serviceAreaId = Number(filterServiceArea.value)
    if (filterKeyword.value) params.keyword = filterKeyword.value
    if (filterStatus.value !== '') params.status = Number(filterStatus.value)
    const res = await merchantApi.getPage(params)
    if (res.code === 0) {
      const data = res.data
      merchantList.value = data.records || data.list || []
      for (const m of merchantList.value) {
        const sa = serviceAreaList.value.find(sa => Number(sa.id) === Number(m.serviceAreaId))
        m.serviceAreaName = sa ? sa.name : '-'
        if (!m.contactName) m.contactName = '无'
        if (!m.address) m.address = '无'
        if (!m.auditRemark) m.auditRemark = '-'
      }
      await attachBoundAccounts()
    }
  } catch (error) {
    ElMessage.error('加载商户失败')
  } finally {
    loading.value = false
  }
}

const attachBoundAccounts = async () => {
  await Promise.all(
    merchantList.value.map(async (m) => {
      try {
        const res = await merchantApi.listAccounts(m.id)
        m.boundAccounts = res.code === 0 ? res.data || [] : []
      } catch {
        m.boundAccounts = []
      }
    }),
  )
}

const openAccountDialog = (row) => {
  accountMerchant.value = row
  accountForm.username = ''
  accountForm.password = ''
  accountForm.email = `merchant${row.id}@example.com`
  accountForm.phone = row.contactPhone && row.contactPhone !== '无' ? row.contactPhone : ''
  accountDialogVisible.value = true
}

const submitAccount = async () => {
  if (!accountFormRef.value || !accountMerchant.value) return
  await accountFormRef.value.validate(async (valid) => {
    if (!valid) return
    accountSubmitting.value = true
    try {
      const payload = {
        username: accountForm.username,
        password: accountForm.password,
        email: accountForm.email || undefined,
        phone: accountForm.phone || undefined,
        status: 1,
      }
      const res = await merchantApi.createAccount(accountMerchant.value.id, payload)
      if (res.code === 0) {
        ElMessage.success(`账号 ${res.data?.username || accountForm.username} 已创建并绑定`)
        accountDialogVisible.value = false
        await loadMerchants()
      } else {
        ElMessage.error(res.message || '创建失败')
      }
    } catch (e) {
      ElMessage.error(e?.response?.data?.message || '创建失败，请确认已执行 merchant:account 权限 SQL 并重启后端')
    } finally {
      accountSubmitting.value = false
    }
  })
}

const handleAdd = () => {
  dialogTitle.value = '新增商户'
  isEdit.value = false
  Object.assign(form, {
    id: null,
    serviceAreaId: null,
    name: '',
    type: '',
    contactName: '',
    contactPhone: '',
    address: '',
    description: '',
    imageUrl: '',
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑商户'
  isEdit.value = true
  Object.assign(form, {
    id: row.id,
    serviceAreaId: row.serviceAreaId,
    name: row.name,
    type: row.type,
    contactName: row.contactName,
    contactPhone: row.contactPhone,
    address: row.address,
    description: row.description,
    imageUrl: row.imageUrl || '',
  })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    let res
    if (isEdit.value) {
      res = await merchantApi.update(form.id, form)
    } else {
      res = await merchantApi.add(form)
    }
    if (res.code === 0) {
      ElMessage.success(isEdit.value ? '修改成功' : '添加成功')
      dialogVisible.value = false
      loadMerchants()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    console.error(error)
  }
}

const handleAudit = (row, status) => {
  currentAuditRow.value = row
  targetAuditStatus.value = status
  auditForm.remark = ''
  auditDialogVisible.value = true
}

const confirmAudit = async (status) => {
  try {
    const res = await merchantApi.audit(currentAuditRow.value.id, {
      status: status,
      remark: auditForm.remark
    })
    if (res.code === 0) {
      ElMessage.success(status === 1 ? '审核通过' : '已拒绝')
      currentAuditRow.value.status = status
      currentAuditRow.value.auditRemark = auditForm.remark || '-'
      auditDialogVisible.value = false
    } else {
      ElMessage.error(res.message || '审核失败')
    }
  } catch (error) {
    ElMessage.error('审核失败')
  }
}

const handleToggleStatus = async (row, newStatus) => {
  try {
    const res = await merchantApi.updateStatus(row.id, newStatus)
    if (res.code === 0) {
      const statusText = newStatus === 1 ? '恢复营业' : newStatus === 2 ? '暂停营业' : '已关闭'
      ElMessage.success(`商户已${statusText}`)
      row.status = newStatus
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该商户吗？', '提示', { type: 'warning' })
    const res = await merchantApi.delete(row.id)
    if (res.code === 0) {
      ElMessage.success('删除成功')
      loadMerchants()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(async () => {
  await loadServiceAreas()
  await loadMerchants()
})
</script>

<style scoped>

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.filters {
  margin-bottom: 20px;
}

.text-gray {
  color: #999;
}

.el-table {
  font-size: 13px;
}

.el-table th {
  background-color: #fafafa;
  font-weight: 500;
}

.el-table td, .el-table th {
  padding: 10px 8px;
}

.el-button--link {
  padding: 0 8px;
}

.account-tag {
  margin-right: 4px;
  margin-bottom: 4px;
}

.bound-list {
  margin-top: 8px;
  padding-top: 12px;
  border-top: 1px solid #ebeef5;
}

.bound-list__title {
  font-size: 13px;
  color: #64748b;
  margin-bottom: 8px;
}
</style>
