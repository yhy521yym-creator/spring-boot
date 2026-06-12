import request from './request'

export function getMeApi() {
  return request.get('/users/me')
}

export function changePasswordApi({ oldPassword, newPassword }) {
  return request.post('/users/change-password', { oldPassword, newPassword })
}

export function getById(id) {
  return request.get(`/users/${id}`)
}

export function list(params) {
  return request.get('/users', { params })
}

export function add(data) {
  return request.post('/users', data)
}

export function update(id, data) {
  return request.put(`/users/${id}`, data)
}

export function remove(id) {
  return request.delete(`/users/${id}`)
}

export function resetPassword(id, data) {
  return request.post(`/users/${id}/reset-password`, data)
}

export const userApi = {
  getById,
  list,
  add,
  update,
  delete: remove,
  resetPassword,
}