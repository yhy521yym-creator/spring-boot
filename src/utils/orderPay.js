/**
 * 统一解析订单支付状态（兼容 payStatus / pay_status 及历史数据）
 * @returns {'PAID'|'UNPAID'|'NONE'}
 */
export function resolvePayStatus(row) {
  const raw = row?.payStatus ?? row?.pay_status
  if (raw === 'PAID' || raw === 'LEGACY') return 'PAID'
  if (raw === 'UNPAID') return 'UNPAID'

  if (row?.status === 'CANCELLED') return 'NONE'
  if (row?.status === 'COMPLETED' || row?.status === 'CONFIRMED') return 'PAID'
  if (row?.status === 'PENDING') return 'UNPAID'
  return 'UNPAID'
}

export function payStatusLabel(row) {
  const s = resolvePayStatus(row)
  if (s === 'PAID') return '已支付'
  if (s === 'UNPAID') return '待支付'
  return '-'
}

export function payStatusTagType(row) {
  const s = resolvePayStatus(row)
  if (s === 'UNPAID') return 'danger'
  if (s === 'PAID') return 'success'
  return 'info'
}

export function canPayOrder(row) {
  return resolvePayStatus(row) === 'UNPAID' && row?.status !== 'CANCELLED' && row?.status !== 'COMPLETED'
}
