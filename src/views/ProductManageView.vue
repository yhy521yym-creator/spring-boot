<template>
  <div class="page-view">
    <el-card shadow="never" class="page-card">
      <template #header>
        <div class="card-header">
          <span>商品管理</span>
          <el-button type="primary" @click="handleAdd">新增商品</el-button>
        </div>
      </template>

      <el-row :gutter="20" class="filters">
        <el-col :span="5">
          <el-select v-model="filterServiceArea" placeholder="选择服务区" clearable @change="handleServiceAreaChange">
            <el-option v-for="sa in serviceAreaList" :key="sa.id" :label="sa.name" :value="sa.id" />
          </el-select>
        </el-col>
        <el-col :span="5">
          <el-select v-model="filterMerchant" placeholder="选择商户" clearable @change="loadProducts">
            <el-option v-for="m in filterMerchantOptions" :key="m.id" :label="m.name" :value="m.id" />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-select v-model="filterType" placeholder="商品类型" clearable @change="loadProducts">
            <el-option label="餐饮" value="RESTAURANT" />
            <el-option label="酒店" value="HOTEL" />
            <el-option label="通用" value="GENERAL" />
          </el-select>
        </el-col>
      </el-row>

      <el-table :data="productList" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
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
        <el-table-column prop="name" label="商品名称" width="150" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeTagType(row.type)">{{ getTypeName(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="merchantName" label="商户" width="120" />
        <el-table-column prop="serviceAreaName" label="服务区" width="120" />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="80" align="center" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">{{ row.status === 1 ? '上架' : '下架' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="success" link size="small" @click="toggleStatus(row)">
              {{ row.status === 1 ? '下架' : '上架' }}
            </el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" destroy-on-close>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="所属服务区" prop="serviceAreaId">
          <el-select v-model="form.serviceAreaId" placeholder="选择服务区" style="width: 100%" @change="handleFormServiceAreaChange">
            <el-option v-for="sa in serviceAreaList" :key="sa.id" :label="sa.name" :value="sa.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属商户" prop="merchantId">
          <el-select v-model="form.merchantId" placeholder="选择商户" style="width: 100%" :disabled="!form.serviceAreaId">
            <el-option v-for="m in filteredMerchants" :key="m.id" :label="m.name" :value="m.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" placeholder="选择类型" style="width: 100%">
            <el-option label="餐饮" value="RESTAURANT" />
            <el-option label="酒店" value="HOTEL" />
            <el-option label="通用" value="GENERAL" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="form.stock" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="商品图片">
          <ImageUpload v-model="form.imageUrl" type="product" tip="建议 1:1，不超过 2MB" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRoute } from 'vue-router'
import { productApi } from '../api/product'
import { merchantApi } from '../api/merchant'
import { getServiceAreaList } from '../api/serviceArea'
import { useUserStore } from '../store/useUserStore'
import ImageUpload from '../components/ImageUpload.vue'
import { resolveImageUrl } from '../utils/image'

const route = useRoute()
const userStore = useUserStore()
const isAdmin = computed(() => ['SUPER_ADMIN', 'OPERATOR'].includes(userStore.role))

const productList = ref([])
const merchantList = ref([])
const serviceAreaList = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('新增商品')
const formRef = ref(null)
const isEdit = ref(false)

const filterServiceArea = ref('')
const filterMerchant = ref('')
const filterType = ref('')

const form = reactive({
  id: null,
  serviceAreaId: null,
  merchantId: null,
  name: '',
  type: '',
  price: 0,
  stock: 0,
  description: '',
  imageUrl: '',
})

const rules = {
  serviceAreaId: [{ required: true, message: '请选择服务区', trigger: 'change' }],
  merchantId: [{ required: true, message: '请选择商户', trigger: 'change' }],
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }]
}

const filteredMerchants = computed(() => {
  if (!form.serviceAreaId) return merchantList.value
  return merchantList.value.filter(m => Number(m.serviceAreaId) === Number(form.serviceAreaId))
})

const filterMerchantOptions = computed(() => {
  if (!filterServiceArea.value) return merchantList.value
  return merchantList.value.filter(m => Number(m.serviceAreaId) === Number(filterServiceArea.value))
})

const getTypeName = (type) => {
  const map = { RESTAURANT: '餐饮', HOTEL: '酒店', GENERAL: '通用' }
  return map[type] || type
}

const getTypeTagType = (type) => {
  const map = { RESTAURANT: 'warning', HOTEL: 'success', GENERAL: 'info' }
  return map[type] || ''
}

const handleServiceAreaChange = () => {
  filterMerchant.value = ''
  loadProducts()
}

const handleFormServiceAreaChange = () => {
  form.merchantId = null
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
    const res = await merchantApi.getList({})
    if (res.code === 0) {
      merchantList.value = res.data || []
    }
  } catch (error) {
    console.error('加载商户失败', error)
  }
}

const loadProducts = async () => {
  try {
    loading.value = true
    const params = { size: 1000 }
    if (filterType.value) params.type = filterType.value
    if (filterMerchant.value) {
      params.merchantId = Number(filterMerchant.value)
    } else if (!isAdmin.value) {
      if (userStore.user?.merchantId) {
        params.merchantId = Number(userStore.user.merchantId)
      }
    }
    const res = await productApi.getList(params)
    if (res.code === 0) {
      productList.value = res.data.records || res.data || []
      for (const p of productList.value) {
        const m = merchantList.value.find(m => Number(m.id) === Number(p.merchantId))
        p.merchantName = m ? m.name : '-'
        if (m && m.serviceAreaId) {
          const sa = serviceAreaList.value.find(sa => Number(sa.id) === Number(m.serviceAreaId))
          p.serviceAreaName = sa ? sa.name : '-'
        } else {
          p.serviceAreaName = '-'
        }
      }
    }
  } catch (error) {
    ElMessage.error('加载商品失败')
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  dialogTitle.value = '新增商品'
  isEdit.value = false
  Object.assign(form, {
    id: null,
    serviceAreaId: null,
    merchantId: null,
    name: '',
    type: '',
    price: 0,
    stock: 0,
    description: '',
    imageUrl: '',
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑商品'
  isEdit.value = true
  const m = merchantList.value.find(mer => mer.id === row.merchantId)
  Object.assign(form, {
    id: row.id,
    serviceAreaId: m ? m.serviceAreaId : null,
    merchantId: row.merchantId,
    name: row.name,
    type: row.type,
    price: row.price,
    stock: row.stock,
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
      res = await productApi.update(form.id, form)
    } else {
      res = await productApi.add(form)
    }
    if (res.code === 0) {
      ElMessage.success(isEdit.value ? '修改成功' : '添加成功')
      dialogVisible.value = false
      loadProducts()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    console.error(error)
  }
}

const toggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  const res = await productApi.updateStatus(row.id, newStatus)
  if (res.code === 0) {
    ElMessage.success(newStatus === 1 ? '已上架' : '已下架')
    loadProducts()
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该商品吗？', '提示', { type: 'warning' })
    const res = await productApi.delete(row.id)
    if (res.code === 0) {
      ElMessage.success('删除成功')
      loadProducts()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(async () => {
  if (!isAdmin.value && !userStore.user?.merchantId) {
    await userStore.fetchMe()
  }
  await loadServiceAreas()
  await loadMerchants()
  const queryMerchantId = route.query.merchantId
  if (queryMerchantId) {
    filterMerchant.value = Number(queryMerchantId)
    const m = merchantList.value.find(mer => mer.id === filterMerchant.value)
    if (m) {
      filterServiceArea.value = m.serviceAreaId
    }
  }
  loadProducts()
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
</style>
