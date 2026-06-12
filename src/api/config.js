import request from './request'

/**
 * 站点配置API
 */
export const configApi = {
  /**
   * 分页查询配置
   * @param {Object} params - 查询参数
   * @param {number} params.pageNum - 页码
   * @param {number} params.pageSize - 每页大小
   * @param {string} params.configKey - 配置键名（模糊搜索）
   * @returns {Promise}
   */
  list: (params) => request.get('/admin/config/list', { params }),

  /**
   * 根据键获取配置值
   * @param {string} key - 配置键名
   * @returns {Promise}
   */
  getValueByKey: (key) => request.get(`/admin/config/${key}`),

  /**
   * 新增或更新配置
   * @param {Object} data - 配置数据
   * @param {string} data.configKey - 配置键名
   * @param {string} data.configValue - 配置值
   * @param {string} data.description - 描述
   * @param {number} data.status - 状态
   * @returns {Promise}
   */
  saveOrUpdate: (data) => request.post('/admin/config', data),

  /**
   * 删除配置
   * @param {number} id - 配置ID
   * @returns {Promise}
   */
  delete: (id) => request.delete(`/admin/config/${id}`),

  /**
   * 获取地图API Key
   * @returns {Promise}
   */
  getMapKey: () => request.get('/admin/config/public/map-key'),
}
