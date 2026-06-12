import request from './request'

export const permissionApi = {
  getTree: () => {
    return request({
      url: '/admin/permissions/tree',
      method: 'get'
    })
  },
  getMenuTree: () => {
    return request({
      url: '/admin/permissions/menus',
      method: 'get'
    })
  },
  add: (data) => {
    return request({
      url: '/admin/permissions',
      method: 'post',
      data
    })
  },
  update: (id, data) => {
    return request({
      url: `/admin/permissions/${id}`,
      method: 'put',
      data
    })
  },
  delete: (id) => {
    return request({
      url: `/admin/permissions/${id}`,
      method: 'delete'
    })
  }
}

export const getMenuTree = permissionApi.getMenuTree
export const getPermissionTree = permissionApi.getTree