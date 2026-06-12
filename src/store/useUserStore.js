import { defineStore } from 'pinia'
import { loginApi, registerApi } from '../api/auth'
import { changePasswordApi, getMeApi } from '../api/user'
import { TOKEN_STORAGE_KEY } from '../api/request'

const USER_STORAGE_KEY = 'user_info'

function parseJwtPayload(token) {
  try {
    const parts = token.split('.')
    if (parts.length !== 3) return null
    const payload = parts[1]
    const decoded = atob(payload.replace(/-/g, '+').replace(/_/g, '/'))
    return JSON.parse(decoded)
  } catch {
    return null
  }
}

function getRoleFromToken(token) {
  const payload = parseJwtPayload(token)
  return payload?.role || ''
}

function getUsernameFromToken(token) {
  const payload = parseJwtPayload(token)
  return payload?.sub || ''
}

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem(TOKEN_STORAGE_KEY) || '',
    user: (() => {
      try {
        const raw = localStorage.getItem(USER_STORAGE_KEY)
        return raw ? JSON.parse(raw) : null
      } catch {
        return null
      }
    })(),
  }),
  getters: {
    isLoggedIn: (state) => !!state.token,
    role: (state) => getRoleFromToken(state.token) || state.user?.role || '',
    username: (state) => getUsernameFromToken(state.token) || state.user?.username || '',
  },
  actions: {
    setToken(token) {
      this.token = token || ''
      if (this.token) {
        localStorage.setItem(TOKEN_STORAGE_KEY, this.token)
      } else {
        localStorage.removeItem(TOKEN_STORAGE_KEY)
      }
    },
    setUser(user) {
      this.user = user || null
      if (user) {
        localStorage.setItem(USER_STORAGE_KEY, JSON.stringify(user))
      } else {
        localStorage.removeItem(USER_STORAGE_KEY)
      }
    },
    async login({ username, password }) {
      const res = await loginApi({ username, password })

      if (res && res.code !== undefined && res.code !== 0) {
        throw new Error(res.message || '登录失败')
      }

      const token = res?.token || res?.jwt || res?.data?.token || res?.data?.jwt
      if (!token) {
        throw new Error(res?.message || '登录接口未返回 token，请检查账号密码或后端服务')
      }
      this.setToken(token)

      const user = res?.user || res?.data?.user || res?.data?.profile
      if (user) {
        this.setUser(user)
      } else {
        await this.fetchMe()
      }
    },
    async fetchMe() {
      try {
        const res = await getMeApi()
        if (res && res.code === 0 && res.data) {
          this.setUser(res.data)
          return
        }
      } catch (e) {
        console.error('fetchMe API error:', e)
      }

      const tokenRole = getRoleFromToken(this.token)
      const tokenUsername = getUsernameFromToken(this.token)
      if (tokenRole || tokenUsername) {
        this.setUser({ username: tokenUsername, role: tokenRole })
      }
    },
    logout() {
      this.setToken('')
      this.setUser(null)
    },
    async changePassword({ oldPassword, newPassword }) {
      return await changePasswordApi({ oldPassword, newPassword })
    },
    async register({ username, password, email }) {
      return await registerApi({ username, password, email })
    },
  },
})