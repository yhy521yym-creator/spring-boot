import request from './request'

// 你后端登录接口路径如果不同，改这里即可
export function loginApi({ username, password }) {
  return request.post('/auth/login', { username, password })
}

export function registerApi({ username, password, email }) {
  return request.post('/auth/register', { username, password, email })
}

