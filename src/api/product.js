import request from './request'

export const productApi = {
  getPage(params) {
    return request.get('/products/page', { params })
  },
  getList(params) {
    return request.get('/products', { params })
  },
  getById(id) {
    return request.get(`/products/${id}`)
  },
  add(data) {
    return request.post('/products', data)
  },
  update(id, data) {
    return request.put(`/products/${id}`, data)
  },
  updateStatus(id, status) {
    return request.put(`/products/${id}/status`, null, { params: { status } })
  },
  delete(id) {
    return request.delete(`/products/${id}`)
  }
}
