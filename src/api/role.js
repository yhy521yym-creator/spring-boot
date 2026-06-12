import request from './request'

/**
 * 角色管理API
 */
export const roleApi = {
  /**
   * 获取所有角色（用于下拉框）
   * @returns {Promise}
   */
  list: () => request.get('/admin/roles/list'),

  /**
   * 分页查询角色
   * @param {Object} params - 查询参数
   * @param {number} params.pageNum - 页码
   * @param {number} params.pageSize - 每页大小
   * @param {string} params.name - 角色名称（模糊搜索）
   * @returns {Promise}
   */
  page: (params) => request.get('/admin/roles/page', { params }),

  /**
   * 新增角色
   * @param {Object} data - 角色数据
   * @param {string} data.name - 角色名称
   * @param {string} data.code - 角色编码
   * @param {number} data.status - 状态
   * @returns {Promise}
   */
  add: (data) => request.post('/admin/roles', data),

  /**
   * 修改角色
   * @param {number} id - 角色ID
   * @param {Object} data - 角色数据
   * @param {string} data.name - 角色名称
   * @param {string} data.code - 角色编码
   * @param {number} data.status - 状态
   * @returns {Promise}
   */
  update: (id, data) => request.put(`/admin/roles/${id}`, data),

  /**
   * 删除角色
   * @param {number} id - 角色ID
   * @returns {Promise}
   */
  delete: (id) => request.delete(`/admin/roles/${id}`),

  /**
   * 获取角色权限
   * @param {number} id - 角色ID
   * @returns {Promise}
   */
  getPermissions: (id) => request.get(`/admin/roles/${id}/permissions`),

  /**
   * 分配权限
   * @param {number} id - 角色ID
   * @param {Object} data - 权限数据
   * @param {Array<number>} data.permissionIds - 权限ID列表
   * @returns {Promise}
   */
  assignPermissions: (id, data) => request.put(`/admin/roles/${id}/permissions`, data),
}
