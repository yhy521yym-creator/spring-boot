import request from './request'

export const merchantApi = {
  getPage(params) {
    return request.get('/merchants/page', { params })
  },
  getList(params) {
    return request.get('/merchants', { params })
  },
  getById(id) {
    return request.get(`/merchants/${id}`)
  },
  add(data) {
    return request.post('/merchants', data)
  },
  update(id, data) {
    return request.put(`/merchants/${id}`, data)
  },
  updateStatus(id, status) {
    return request.put(`/merchants/${id}/status?status=${status}`)
  },
  audit(id, data) {
    return request.put(`/merchants/${id}/audit`, data)
  },
  delete(id) {
    return request.delete(`/merchants/${id}`)
  },
  getStatistics() {
    return request.get('/merchants/statistics')
  },
  listAccounts(merchantId) {
    return request.get(`/merchants/${merchantId}/accounts`)
  },
  createAccount(merchantId, data) {
    return request.post(`/merchants/${merchantId}/account`, data)
  },
}
