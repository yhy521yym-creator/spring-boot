import request from './request'

export const orderApi = {
  getPage(params) {
    return request.get('/orders/page', { params })
  },
  getList(params) {
    return request.get('/orders', { params })
  },
  getById(id) {
    return request.get(`/orders/${id}`)
  },
  getByOrderNo(orderNo) {
    return request.get(`/orders/no/${orderNo}`)
  },
  add(data) {
    return request.post('/orders', data)
  },
  /** @deprecated 请使用 bookingApi.create，避免与 /orders/{id} 路由冲突 */
  bookWithReservation(data) {
    return request.post('/bookings', data)
  },
  update(id, data) {
    return request.put(`/orders/${id}`, data)
  },
  updateStatus(id, status) {
    return request.put(`/orders/${id}/status`, null, { params: { status } })
  },
  cancel(id) {
    return request.delete(`/orders/${id}`)
  },
  pay(id, data = {}) {
    return request.post(`/payments/order/${id}`, data)
  },
  getStatistics() {
    return request.get('/orders/statistics')
  }
}
