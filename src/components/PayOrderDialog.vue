<template>
  <el-dialog
    :model-value="visible"
    title="模拟支付"
    width="460px"
    destroy-on-close
    @update:model-value="(v) => !v && emit('close')"
  >
    <div v-if="order" class="pay-body">
      <div class="pay-amount">
        <span class="label">应付金额（下单时确定）</span>
        <span class="price">¥{{ amountText }}</span>
      </div>
      <el-descriptions :column="1" border size="small" class="pay-info">
        <el-descriptions-item label="订单号">{{ order.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="商品">{{ order.productName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="商户">{{ order.merchantName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="数量">{{ order.quantity || 1 }} 件</el-descriptions-item>
        <el-descriptions-item label="金额明细">
          单价约 ¥{{ unitPriceText }} × {{ order.quantity || 1 }} = ¥{{ amountText }}
        </el-descriptions-item>
      </el-descriptions>
      <p class="pay-hint">
        支付金额 = 商品单价 × 预约数量，与订单列表「应付金额」一致。本功能为毕设演示，不会产生真实扣款。
      </p>
      <el-radio-group v-model="payMethod" class="pay-methods">
        <el-radio value="WECHAT" border>
          <span class="method-item">微信支付</span>
        </el-radio>
        <el-radio value="ALIPAY" border>
          <span class="method-item">支付宝</span>
        </el-radio>
        <el-radio value="SIMULATE" border>
          <span class="method-item">模拟钱包</span>
        </el-radio>
      </el-radio-group>
    </div>
    <template #footer>
      <el-button @click="emit('close')">取消</el-button>
      <el-button type="primary" :loading="paying" @click="handlePay">
        确认支付 ¥{{ amountText }}
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { orderApi } from '../api/order'
import { resolvePayStatus } from '../utils/orderPay'

const props = defineProps({
  visible: { type: Boolean, default: false },
  order: { type: Object, default: null },
})

const emit = defineEmits(['close', 'success'])

const payMethod = ref('WECHAT')
const paying = ref(false)

const amountText = computed(() => {
  const n = Number(props.order?.totalPrice)
  return Number.isFinite(n) ? n.toFixed(2) : '0.00'
})

const unitPriceText = computed(() => {
  const q = Number(props.order?.quantity) || 1
  const n = Number(props.order?.totalPrice)
  if (!Number.isFinite(n)) return '0.00'
  return (n / q).toFixed(2)
})

watch(
  () => props.visible,
  (v) => {
    if (v) payMethod.value = 'WECHAT'
  },
)

const handlePay = async () => {
  if (!props.order?.id) return
  if (resolvePayStatus(props.order) === 'PAID') {
    ElMessage.info('订单已支付')
    emit('close')
    return
  }
  paying.value = true
  try {
    await new Promise((r) => setTimeout(r, 800))
    const res = await orderApi.pay(props.order.id, { payMethod: payMethod.value })
    if (res.code === 0) {
      ElMessage.success(`支付成功，实付 ¥${amountText.value}`)
      emit('success', res.data)
      emit('close')
    } else {
      ElMessage.error(res.message || '支付失败')
    }
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '支付失败')
  } finally {
    paying.value = false
  }
}
</script>

<style scoped>
.pay-body {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.pay-amount {
  text-align: center;
  padding: 12px 0;
  background: #fffbeb;
  border-radius: 8px;
  border: 1px solid #fde68a;
}

.pay-amount .label {
  display: block;
  font-size: 13px;
  color: #64748b;
  margin-bottom: 4px;
}

.pay-amount .price {
  font-size: 32px;
  font-weight: 700;
  color: #d97706;
}

.pay-hint {
  margin: 0;
  font-size: 12px;
  color: #909399;
  line-height: 1.5;
}

.pay-methods {
  display: flex;
  flex-direction: column;
  gap: 10px;
  width: 100%;
}

.pay-methods :deep(.el-radio) {
  margin-right: 0;
  width: 100%;
  height: auto;
  padding: 10px 12px;
}

.method-item {
  font-size: 14px;
}
</style>
