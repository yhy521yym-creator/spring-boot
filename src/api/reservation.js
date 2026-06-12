import request from './request'

export const reservationApi = {
  getPage(params) {
    return request.get('/reservations/page', { params })
  },
  getList(params) {
    return request.get('/reservations', { params })
  },
  getById(id) {
    return request.get(`/reservations/${id}`)
  },
  add(data) {
    return request.post('/reservations', data)
  },
  update(id, data) {
    return request.put(`/reservations/${id}`, data)
  },
  updateStatus(id, status) {
    return request.put(`/reservations/${id}/status`, null, { params: { status } })
  },
  delete(id) {
    return request.delete(`/reservations/${id}`)
  },
  getStatistics() {
    return request.get('/reservations/statistics')
  }
}
