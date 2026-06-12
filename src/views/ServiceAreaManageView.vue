<template>
  <div class="page-view">
    <div class="page-head">
      <div class="page-head__main">
        <h1 class="page-head__title">服务区管理</h1>
        <p class="page-head__desc">维护服务区基础信息、设施与状态</p>
      </div>
      <div class="page-head__extra">
        <el-button type="primary" @click="handleAdd" v-if="isAdmin">
          <el-icon><Plus /></el-icon>
          新增服务区
        </el-button>
        <el-button type="success" @click="handleExport">
          <el-icon><Download /></el-icon>
          导出 Excel
        </el-button>
      </div>
    </div>

    <el-card class="search-card page-card" shadow="never">
      <el-form :model="searchForm" inline>
        <el-form-item label="服务区名称">
          <el-input
            v-model="searchForm.name"
            placeholder="请输入服务区名称"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="地址">
          <el-input
            v-model="searchForm.address"
            placeholder="请输入地址"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card page-card" shadow="never">
      <el-table
        :data="tableData"
        v-loading="loading"
        stripe
        border
        style="width: 100%"
      >
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="name" label="服务区名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="address" label="地址" min-width="220" show-overflow-tooltip />
        <el-table-column prop="longitude" label="经度" width="100" align="center" />
        <el-table-column prop="latitude" label="纬度" width="100" align="center" />
        <el-table-column label="设施" min-width="200">
          <template #default="{ row }">
            <el-tag
              v-for="facility in parseFacilities(row.facilities)"
              :key="facility"
              size="small"
              class="facility-tag"
            >
              {{ facility }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" align="center" />
        <el-table-column label="操作" width="240" align="center" fixed="right" v-if="isAdmin">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">
              <el-icon><View /></el-icon>
              查看
            </el-button>
            <el-button type="primary" link @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="服务区名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入服务区名称" :disabled="isViewOnly" />
        </el-form-item>
        <el-form-item label="详细地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入详细地址" :disabled="isViewOnly" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="经度" prop="longitude">
              <el-input-number
                v-model="form.longitude"
                :precision="7"
                :step="0.0000001"
                :min="-180"
                :max="180"
                style="width: 100%"
                placeholder="请输入经度"
                :disabled="isViewOnly"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="纬度" prop="latitude">
              <el-input-number
                v-model="form.latitude"
                :precision="7"
                :step="0.0000001"
                :min="-90"
                :max="90"
                style="width: 100%"
                placeholder="请输入纬度"
                :disabled="isViewOnly"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="设施">
          <el-checkbox-group v-model="form.facilitiesList" :disabled="isViewOnly">
            <el-checkbox label="餐厅" />
            <el-checkbox label="加油站" />
            <el-checkbox label="卫生间" />
            <el-checkbox label="超市" />
            <el-checkbox label="汽修" />
            <el-checkbox label="充电桩" />
            <el-checkbox label="休息区" />
            <el-checkbox label="住宿" />
            <el-checkbox label="医疗" />
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入服务区描述"
            :disabled="isViewOnly"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status" :disabled="isViewOnly">
            <el-radio :value="1">正常</el-radio>
            <el-radio :value="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span v-if="isViewOnly" style="color: #909399; font-size: 12px;">
          仅供查看，无法编辑
        </span>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="goToReservation" v-if="isViewOnly">
          预约服务
        </el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading" v-if="!isViewOnly">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Edit, Delete, Download, View } from '@element-plus/icons-vue'
import { useUserStore } from '../store/useUserStore'
import {
  getServiceAreaList,
  getServiceAreaById,
  addServiceArea,
  updateServiceArea,
  deleteServiceArea,
  exportServiceAreas,
} from '../api/serviceArea'

const router = useRouter()
const route = useRoute()

const userStore = useUserStore()

// 判断是否为管理员
const isAdmin = computed(() => {
  return ['SUPER_ADMIN', 'OPERATOR'].includes(userStore.role)
})

// 搜索表单
const searchForm = reactive({
  name: '',
  address: '',
})

// 分页参数
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0,
})

// 表格数据
const tableData = ref([])
const loading = ref(false)

// 对话框相关
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const submitLoading = ref(false)
const isEdit = ref(false)
const isViewOnly = ref(false)
const currentId = ref(null)

// 表单数据
const form = reactive({
  name: '',
  address: '',
  longitude: null,
  latitude: null,
  facilitiesList: [],
  description: '',
  status: 1,
})

// 表单校验规则
const formRules = {
  name: [{ required: true, message: '请输入服务区名称', trigger: 'blur' }],
  address: [{ required: true, message: '请输入详细地址', trigger: 'blur' }],
  longitude: [{ required: true, message: '请输入经度', trigger: 'blur' }],
  latitude: [{ required: true, message: '请输入纬度', trigger: 'blur' }],
}

// 解析设施JSON
const parseFacilities = (facilities) => {
  if (!facilities) return []
  try {
    return JSON.parse(facilities)
  } catch {
    return facilities.split(',').filter(Boolean)
  }
}

// 获取列表数据
const fetchList = async () => {
  loading.value = true
  try {
    console.log('[ServiceAreaManage] Fetching list...')
    const res = await getServiceAreaList({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      name: searchForm.name,
      address: searchForm.address,
    })
    console.log('[ServiceAreaManage] Response:', res)
    if (res.code === 0) {
      tableData.value = res.data.records || []
      pagination.total = res.data.total || 0
      console.log('[ServiceAreaManage] Data loaded successfully')
    } else {
      ElMessage.error(res.message || '获取数据失败')
    }
  } catch (error) {
    console.error('[ServiceAreaManage] Error fetching list:', error)
    // 不要在错误时跳转登录页面，让用户看到错误信息
    // 只有 401 错误才会在 request.js 中处理
    ElMessage.error('获取数据失败: ' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.pageNum = 1
  fetchList()
}

// 重置
const handleReset = () => {
  searchForm.name = ''
  searchForm.address = ''
  pagination.pageNum = 1
  fetchList()
}

// 分页大小变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  fetchList()
}

// 页码变化
const handlePageChange = (page) => {
  pagination.pageNum = page
  fetchList()
}

// 新增
const handleAdd = () => {
  isEdit.value = false
  isViewOnly.value = false
  currentId.value = null
  dialogTitle.value = '新增服务区'
  resetForm()
  dialogVisible.value = true
}

// 查看详情
const handleView = async (row) => {
  try {
    const res = await getServiceAreaById(row.id)
    if (res.code === 0) {
      Object.assign(form, {
        name: res.data.name,
        address: res.data.address,
        longitude: res.data.longitude,
        latitude: res.data.latitude,
        facilitiesList: parseFacilities(res.data.facilities),
        description: res.data.description,
        status: res.data.status,
      })
      isEdit.value = false
      isViewOnly.value = true
      currentId.value = row.id
      dialogTitle.value = '查看服务区'
      dialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取服务区详情失败')
  }
}

// 跳转到预约管理
const goToReservation = () => {
  dialogVisible.value = false
  if (currentId.value) {
    router.push({
      path: '/reservation',
      query: { serviceAreaId: currentId.value }
    })
  } else {
    router.push('/reservation')
  }
}

// 编辑
const handleEdit = (row) => {
  isEdit.value = true
  isViewOnly.value = false
  currentId.value = row.id
  dialogTitle.value = '编辑服务区'
  Object.assign(form, {
    name: row.name,
    address: row.address,
    longitude: row.longitude,
    latitude: row.latitude,
    facilitiesList: parseFacilities(row.facilities),
    description: row.description,
    status: row.status,
  })
  dialogVisible.value = true
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除服务区"${row.name}"吗？`,
    '确认删除',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(async () => {
      try {
        const res = await deleteServiceArea(row.id)
        if (res.code === 0) {
          ElMessage.success('删除成功')
          fetchList()
        } else {
          ElMessage.error(res.message || '删除失败')
        }
      } catch (error) {
        ElMessage.error('删除失败')
      }
    })
    .catch(() => {})
}

// 重置表单
const resetForm = () => {
  form.name = ''
  form.address = ''
  form.longitude = null
  form.latitude = null
  form.facilitiesList = []
  form.description = ''
  form.status = 1
}

// 提交表单
const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    const data = {
      name: form.name,
      address: form.address,
      longitude: form.longitude,
      latitude: form.latitude,
      facilities: JSON.stringify(form.facilitiesList),
      description: form.description,
      status: form.status,
    }

    let res
    if (isEdit.value) {
      res = await updateServiceArea(currentId.value, data)
    } else {
      res = await addServiceArea(data)
    }

    if (res.code === 0) {
      ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
      dialogVisible.value = false
      fetchList()
    } else {
      ElMessage.error(res.message || (isEdit.value ? '修改失败' : '新增失败'))
    }
  } catch (error) {
    ElMessage.error(isEdit.value ? '修改失败' : '新增失败')
  } finally {
    submitLoading.value = false
  }
}

// 导出Excel
const handleExport = () => {
  exportServiceAreas(searchForm.name, searchForm.address)
    .then(() => {
      ElMessage.success('导出成功')
    })
    .catch(() => {
      ElMessage.error('导出失败')
    })
}

onMounted(async () => {
  fetchList()

  const viewId = route.query.viewId
  if (viewId) {
    try {
      const res = await getServiceAreaById(viewId)
      if (res.code === 0) {
        Object.assign(form, {
          name: res.data.name,
          address: res.data.address,
          longitude: res.data.longitude,
          latitude: res.data.latitude,
          facilitiesList: parseFacilities(res.data.facilities),
          description: res.data.description,
          status: res.data.status,
        })
        isEdit.value = false
        isViewOnly.value = true
        currentId.value = Number(viewId)
        dialogTitle.value = '查看服务区'
        dialogVisible.value = true
      }
    } catch (error) {
      ElMessage.error('获取服务区详情失败')
    }
  }
})
</script>

<style scoped>
.service-area-manage {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
}

.search-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.facility-tag {
  margin-right: 5px;
  margin-bottom: 5px;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
