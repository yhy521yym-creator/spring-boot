import request from './request'

/**
 * 分页查询服务区列表
 * @param {Object} params - 查询参数
 * @param {number} params.pageNum - 页码
 * @param {number} params.pageSize - 每页数量
 * @param {string} params.name - 服务区名称（可选）
 * @param {string} params.address - 地址（可选）
 * @returns {Promise}
 */
export function getServiceAreaList(params) {
  return request.get('/service-areas', { params })
}

/**
 * 获取服务区详情
 * @param {number} id - 服务区ID
 * @returns {Promise}
 */
export function getServiceAreaById(id) {
  return request.get(`/service-areas/${id}`)
}

/**
 * 新增服务区
 * @param {Object} data - 服务区数据
 * @returns {Promise}
 */
export function addServiceArea(data) {
  return request.post('/service-areas', data)
}

/**
 * 修改服务区
 * @param {number} id - 服务区ID
 * @param {Object} data - 服务区数据
 * @returns {Promise}
 */
export function updateServiceArea(id, data) {
  return request.put(`/service-areas/${id}`, data)
}

/**
 * 删除服务区
 * @param {number} id - 服务区ID
 * @returns {Promise}
 */
export function deleteServiceArea(id) {
  return request.delete(`/service-areas/${id}`)
}

/**
 * 查询附近的服务区
 * @param {Object} params - 查询参数
 * @param {number} params.lat - 纬度
 * @param {number} params.lng - 经度
 * @param {number} params.radius - 半径（米）
 * @returns {Promise}
 */
export function getNearbyServiceAreas(params) {
  return request.get('/service-areas/nearby', { params })
}

/**
 * 导出服务区列表为Excel
 * @param {string} name - 服务区名称（可选）
 * @param {string} address - 地址（可选）
 * @returns {Promise}
 */
export function exportServiceAreas(name, address) {
  return request({
    url: '/export/service-areas',
    method: 'GET',
    params: { name, address },
    responseType: 'blob'
  }).then(response => {
    const blob = new Blob([response], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `服务区列表_${new Date().getTime()}.xlsx`
    document.body.appendChild(a)
    a.click()
    window.URL.revokeObjectURL(url)
    document.body.removeChild(a)
  })
}
