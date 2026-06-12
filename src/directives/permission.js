import { useUserStore } from '../store/useUserStore'

/**
 * 权限指令
 * 使用方式：v-permission="'user:add'"
 */
export const permissionDirective = {
  mounted(el, binding) {
    const userStore = useUserStore()
    const permission = binding.value

    if (!hasPermission(permission)) {
      el.style.display = 'none'
    }
  },
  updated(el, binding) {
    const userStore = useUserStore()
    const permission = binding.value

    if (!hasPermission(permission)) {
      el.style.display = 'none'
    } else {
      el.style.display = ''
    }
  }
}

/**
 * 检查用户是否有指定权限
 * @param {string} permission - 权限编码
 * @returns {boolean}
 */
function hasPermission(permission) {
  const userStore = useUserStore()
  const permissions = userStore.permissions || []

  // 超级管理员拥有所有权限
  if (userStore.role === 'SUPER_ADMIN') {
    return true
  }

  return permissions.includes(permission)
}
