import axios from 'axios'
import { ElMessage } from 'element-plus'

// 统一 JWT 存储 key（与 Pinia store 保持一致）
export const TOKEN_STORAGE_KEY = 'token'

const baseURL = import.meta.env.VITE_API_BASE_URL || '/api'

const request = axios.create({
  baseURL,
  timeout: 15000,
})

request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem(TOKEN_STORAGE_KEY)
    if (token) {
      config.headers = config.headers || {}
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error),
)

request.interceptors.response.use(
  (response) => response.data,
  (error) => {
    const status = error?.response?.status
    if (status === 401) {
      localStorage.removeItem(TOKEN_STORAGE_KEY)
      if (window.location.pathname !== '/login') {
        window.location.href = '/login'
      }
    }
    if (status === 403) {
      const msg = error?.response?.data?.message || '无权限访问'
      ElMessage.error(msg)
      if (/店铺.*暂停|店铺.*关闭|店铺.*待审核|未绑定店铺/.test(msg)) {
        localStorage.removeItem(TOKEN_STORAGE_KEY)
        if (window.location.pathname !== '/login') {
          setTimeout(() => { window.location.href = '/login' }, 1500)
        }
      }
    }
    return Promise.reject(error)
  },
)

export default request

