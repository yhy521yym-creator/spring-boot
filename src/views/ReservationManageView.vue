<template>
  <div class="page-view">
    <el-card shadow="never" class="page-card">
      <template #header>
        <div class="card-header">
          <span>服务预约</span>
          <el-select
            v-model="serviceAreaId"
            placeholder="选择服务区"
            clearable
            filterable
            style="width: 200px; margin-left: 20px;"
            @change="handleServiceAreaChange"
          >
            <el-option label="全部服务区" value="" />
            <el-option
              v-for="item in serviceAreaList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </div>
      </template>

      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="餐饮商品" name="RESTAURANT">
          <template #label>
            <el-icon><Food /></el-icon>
            <span>餐饮</span>
          </template>
        </el-tab-pane>
        <el-tab-pane label="酒店" name="HOTEL">
          <template #label>
            <el-icon><House /></el-icon>
            <span>酒店</span>
          </template>
        </el-tab-pane>
        <el-tab-pane label="通用商品" name="GENERAL">
          <template #label>
            <el-icon><Goods /></el-icon>
            <span>通用</span>
          </template>
        </el-tab-pane>
      </el-tabs>

      <el-card v-if="selectedServiceArea" class="service-area-info" shadow="never">
        <div class="sa-info">
          <div class="sa-header">
            <el-icon class="sa-icon"><Location /></el-icon>
            <span class="sa-name">{{ selectedServiceArea.name }}</span>
            <el-tag size="small" type="success">服务区</el-tag>
          </div>
          <div class="sa-details">
            <span><el-icon><Place /></el-icon>{{ selectedServiceArea.address || '地址未知' }}</span>
            <span v-if="selectedServiceArea.phone"><el-icon><Phone /></el-icon>{{ selectedServiceArea.phone }}</span>
          </div>
        </div>
      </el-card>

      <el-row :gutter="20" class="product-list">
        <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="product in productList" :key="product.id">
          <el-card class="product-card" shadow="hover">
            <template #header>
              <div class="product-header">
                <span class="product-name">{{ product.name }}</span>
                <el-tag size="small" :type="getTypeTagType(product.type)">
                  {{ getTypeName(product.type) }}
                </el-tag>
              </div>
            </template>
            <div class="product-body">
              <p><el-icon><Shop /></el-icon> 商户: {{ product.merchantName }}</p>
              <p v-if="product.serviceAreaName"><el-icon><Location /></el-icon> 服务区: {{ product.serviceAreaName }}</p>
              <p class="description">{{ product.description }}</p>
              <p class="price">¥{{ product.price }}</p>
            </div>
            <template #footer>
              <el-button type="primary" class="reserve-btn" @click="handleReserve(product)">立即预约</el-button>
            </template>
          </el-card>
        </el-col>
        <el-col :span="24" v-if="productList.length === 0">
          <el-empty description="暂无可预约商品"></el-empty>
        </el-col>
      </el-row>
    </el-card>
  </div>

  <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" destroy-on-close>
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="服务区">
        <el-input v-model="form.serviceAreaName" disabled />
      </el-form-item>
      <el-form-item label="商户">
        <el-input v-model="form.merchantName" disabled />
      </el-form-item>
      <el-form-item label="商品">
        <el-input v-model="form.productName" disabled />
      </el-form-item>
      <el-form-item label="价格">
        <span class="form-price">¥{{ form.price }}</span>
      </el-form-item>
      <el-form-item label="预约人数" prop="quantity">
        <el-input-number v-model="form.quantity" :min="1" :max="20" />
      </el-form-item>
      <el-form-item label="联系人" prop="contactName">
        <el-input v-model="form.contactName" placeholder="请输入联系人" />
      </el-form-item>
      <el-form-item label="联系电话" prop="contactPhone">
        <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
      </el-form-item>
      <el-form-item v-if="form.type === 'HOTEL'" label="入住日期" prop="reservationDate">
        <el-date-picker v-model="form.reservationDate" type="date" placeholder="选择日期" style="width: 100%" value-format="YYYY-MM-DD" :disabled-date="disablePastDates" />
      </el-form-item>
      <el-form-item v-if="form.type === 'HOTEL'" label="离店日期">
        <el-date-picker v-model="form.endDate" type="date" placeholder="选择日期" style="width: 100%" value-format="YYYY-MM-DD" :disabled-date="disablePastDates" />
      </el-form-item>
      <el-form-item v-if="form.type === 'RESTAURANT'" label="预约日期" prop="reservationDate">
        <el-date-picker v-model="form.reservationDate" type="date" placeholder="选择日期" style="width: 100%" value-format="YYYY-MM-DD" :disabled-date="disablePastDates" />
      </el-form-item>
      <el-form-item v-if="form.type === 'RESTAURANT'" label="预约时间" prop="reservationTime">
        <el-time-picker v-model="form.reservationTime" placeholder="选择时间" style="width: 100%" value-format="HH:mm:ss" />
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注（可选）" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleSubmit">提交预约</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Food, House, Goods, Location, Shop, Place, Phone } from '@element-plus/icons-vue'
import { productApi } from '../api/product'
import { merchantApi } from '../api/merchant'
import { bookingApi } from '../api/booking'
import { getServiceAreaList } from '../api/serviceArea'
import { useUserStore } from '../store/useUserStore'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('RESTAURANT')
const productList = ref([])
const serviceAreaList = ref([])
const merchantList = ref([])
const serviceAreaId = ref('')
const selectedServiceArea = computed(() => {
  if (!serviceAreaId.value || serviceAreaId.value === '') return null
  return serviceAreaList.value.find(item => Number(item.id) === Number(serviceAreaId.value)) || null
})
const dialogVisible = ref(false)
const dialogTitle = ref('预约')
const formRef = ref(null)

const form = reactive({
  productId: null,
  merchantId: null,
  serviceAreaId: null,
  productName: '',
  merchantName: '',
  serviceAreaName: '',
  price: '',
  type: '',
  contactName: '',
  contactPhone: '',
  quantity: 1,
  reservationDate: '',
  reservationTime: '',
  endDate: '',
  remark: ''
})

const rules = {
  contactName: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  reservationDate: [{ required: true, message: '请选择日期', trigger: 'change' }],
  reservationTime: [{ required: true, message: '请选择时间', trigger: 'change' }],
  quantity: [{ required: true, message: '请选择人数', trigger: 'change' }]
}

const getTypeName = (type) => {
  const map = { RESTAURANT: '餐饮', HOTEL: '酒店', GENERAL: '通用' }
  return map[type] || type
}

const getTypeTagType = (type) => {
  const map = { RESTAURANT: 'warning', HOTEL: 'success', GENERAL: 'info' }
  return map[type] || ''
}

const disablePastDates = (date) => {
  return date < new Date(new Date().setHours(0, 0, 0, 0))
}

const loadProducts = async () => {
  try {
    const res = await productApi.getList({ type: activeTab.value, status: 1, size: 1000 })
    if (res.code === 0) {
      let products = res.data.records || res.data
      console.log('API返回商品数:', products.length)
      console.log('当前商户列表:', merchantList.value.map(m => ({id: m.id, name: m.name, serviceAreaId: m.serviceAreaId})))
      console.log('选择的服务区ID:', serviceAreaId.value, typeof serviceAreaId.value)
      const validProducts = []
      for (const product of products) {
        const merchantId = Number(product.merchantId || product.merchant_id)
        const merchant = merchantList.value.find(m => Number(m.id) === merchantId)
        if (merchant) {
          product.merchantName = merchant.name
          product.serviceAreaName = merchant.serviceAreaName || merchant.service_area_name || '-'
          product.serviceAreaId = Number(merchant.serviceAreaId || merchant.service_area_id)
          validProducts.push(product)
        }
      }
      console.log('有效商品详情:', validProducts.map(p => ({id: p.id, merchantName: p.merchantName, serviceAreaId: p.serviceAreaId, typeof_saId: typeof p.serviceAreaId})))
      console.log('有效商品数:', validProducts.length)
      products = validProducts
      if (serviceAreaId.value && serviceAreaId.value !== '') {
        console.log('开始筛选, 比较:', Number(serviceAreaId.value), 'vs', validProducts.map(p => Number(p.serviceAreaId)))
        const filtered = products.filter(p => Number(p.serviceAreaId) === Number(serviceAreaId.value))
        console.log('筛选后商品数:', filtered.length, '服务区ID:', serviceAreaId.value)
        products = filtered
      }
      productList.value = products
    }
  } catch (error) {
    console.error('加载商品失败', error)
    ElMessage.error('加载商品失败')
  }
}

const loadMerchants = async () => {
  try {
    const res = await merchantApi.getList({})
    if (res.code === 0) {
      const merchants = res.data || []
      for (const m of merchants) {
        const saId = Number(m.serviceAreaId || m.service_area_id)
        const sa = serviceAreaList.value.find(sa => Number(sa.id) === saId)
        m.serviceAreaName = sa ? sa.name : ''
        m.serviceAreaId = saId
      }
      merchantList.value = merchants
    }
  } catch (error) {
    console.error('加载商户失败', error)
  }
}

const handleTabChange = () => {
  loadProducts()
}

const handleServiceAreaChange = () => {
  loadProducts()
}

const loadServiceAreas = async () => {
  try {
    const res = await getServiceAreaList({ pageNum: 1, pageSize: 100 })
    if (res.code === 0) {
      serviceAreaList.value = res.data.records || res.data.list || []
    }
  } catch (error) {
    console.error('加载服务区列表失败', error)
  }
}

const handleReserve = (product) => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  dialogTitle.value = `预约 - ${product.name}`
  form.productId = product.id
  form.merchantId = product.merchantId
  form.productName = product.name
  form.merchantName = product.merchantName || '-'
  form.serviceAreaName = product.serviceAreaName || '-'
  form.price = product.price
  form.type = product.type
  form.contactName = userStore.user?.username || ''
  form.contactPhone = userStore.user?.phone || ''
  form.quantity = 1
  form.reservationDate = ''
  form.reservationTime = ''
  form.endDate = ''
  form.remark = ''
  dialogVisible.value = true
}

const handleSubmit = async () => {
  try {
    if (!userStore.user?.id) {
      await userStore.fetchMe()
    }
    if (form.type === 'GENERAL' && !form.reservationDate) {
      const today = new Date()
      form.reservationDate = today.toISOString().slice(0, 10)
    }
    if (form.type === 'RESTAURANT') {
      await formRef.value.validate()
    } else if (form.type === 'HOTEL') {
      await formRef.value.validate()
    } else {
      await formRef.value.validateField(['contactName', 'contactPhone', 'quantity'])
    }

    const bookRes = await bookingApi.create({
      merchantId: form.merchantId,
      productId: form.productId,
      quantity: form.quantity,
      totalPrice: Number(form.price) * form.quantity,
      reservationDate: form.reservationDate || null,
      reservationTime: form.reservationTime || null,
      contactName: form.contactName,
      contactPhone: form.contactPhone,
      remark: form.remark,
      duration: form.type === 'HOTEL' ? 1440 : 60,
      reservationRemark: form.remark || `商品预约：${form.productName}`
    })
    if (bookRes.code !== 0) {
      ElMessage.error(bookRes.message || '预约失败')
      return
    }

    const orderNo = bookRes.data?.orderNo || ''
    const orderId = bookRes.data?.orderId
    const payAmount = (Number(form.price) * form.quantity).toFixed(2)
    dialogVisible.value = false
    try {
      await ElMessageBox.confirm(
        orderNo
          ? `预约成功，订单号：${orderNo}\n应付金额：¥${payAmount}（${form.productName} × ${form.quantity}）\n是否立即模拟支付？`
          : `预约成功，应付金额：¥${payAmount}\n是否立即模拟支付？`,
        '模拟支付',
        { confirmButtonText: `支付 ¥${payAmount}`, cancelButtonText: '稍后支付', type: 'info' },
      )
      router.push(orderId ? `/order?payId=${orderId}` : '/order')
    } catch {
      ElMessage.success(
        orderNo
          ? `预约成功，订单号：${orderNo}，待支付 ¥${payAmount}`
          : `预约成功，待支付 ¥${payAmount}`,
      )
      router.push('/order')
    }
  } catch (error) {
    console.error(error)
    const msg =
      error?.response?.data?.message ||
      error?.message ||
      '预约失败，请确认后端已重启并重新登录后再试'
    ElMessage.error(msg)
  }
}

onMounted(async () => {
  if (userStore.isLoggedIn && !userStore.user?.id) {
    await userStore.fetchMe()
  }
  await loadServiceAreas()
  await loadMerchants()
  const queryServiceAreaId = route.query.serviceAreaId
  if (queryServiceAreaId) {
    serviceAreaId.value = Number(queryServiceAreaId)
  }
  await loadProducts()
})
</script>

<style scoped>

.card-header {
  display: flex;
  align-items: center;
}

.product-list {
  margin-top: 16px;
}

.service-area-info {
  margin-bottom: 16px;
  background: linear-gradient(135deg, #f0f9eb 0%, #e8f5e1 100%);
  border: 1px solid #d4edda;
}

.sa-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.sa-header {
  display: flex;
  align-items: center;
  gap: 8px;
}

.sa-icon {
  font-size: 20px;
  color: #67c23a;
}

.sa-name {
  font-size: 16px;
  font-weight: 600;
  color: #1a202c;
}

.sa-details {
  display: flex;
  gap: 20px;
  font-size: 13px;
  color: #606266;
}

.sa-details span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.sa-details .el-icon {
  color: #909399;
}

.product-card {
  margin-bottom: 20px;
  transition: all 0.3s ease;
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.product-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.product-name {
  font-weight: 600;
  font-size: 15px;
}

.product-body p {
  margin: 8px 0;
  font-size: 13px;
  color: #606266;
  display: flex;
  align-items: center;
}

.product-body .description {
  color: #909399;
  font-size: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-body .price {
  font-size: 22px;
  font-weight: bold;
  color: #f56c6c;
  margin-top: 12px;
}

.product-body .el-icon {
  margin-right: 5px;
}

.reserve-btn {
  width: 100%;
}

.form-price {
  font-size: 18px;
  font-weight: bold;
  color: #f56c6c;
}
</style>
