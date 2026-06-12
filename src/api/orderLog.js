import request from './request'

export const orderLogApi = {
  getPage(params) {
    return request.get('/order-logs/page', { params })
  },
  getByOrderId(orderId) {
    return request.get(`/orders/${orderId}/logs`)
  },
}
