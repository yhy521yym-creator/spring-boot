<template>
  <div class="page" :style="{ backgroundImage: `url(${bgImg})` }">
    <div class="card">
      <div class="card__header">
        <div class="card__logo">
          <div class="logo-icon">🚀</div>
        </div>
        <div class="card__title">高速服务区服务管理系统</div>
        <div class="card__sub">请输入账号密码登录</div>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" size="large" class="form"
        @keyup.enter="onSubmit">
        <el-form-item label="用户名" prop="username">
          <el-input 
            v-model="form.username" 
            placeholder="请输入用户名" 
            autocomplete="username"
            prefix-icon="User"
            class="input-styled"
          />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input 
            v-model="form.password" 
            placeholder="请输入密码" 
            type="password" 
            show-password
            autocomplete="current-password"
            prefix-icon="Lock"
            class="input-styled"
          />
        </el-form-item>

        <div class="remember-row">
          <el-checkbox v-model="rememberMe" size="large">记住账号密码</el-checkbox>
        </div>

        <el-button type="primary" class="submit" :loading="loading" @click="onSubmit">
          <span v-if="!loading">立即登录</span>
          <span v-else>登录中...</span>
        </el-button>

        <div class="register-hint">
          还没有账号？<el-link type="primary" @click="goToRegister">立即注册</el-link>
        </div>

        <div class="hint">
          高速服务区智能管理平台 © 2026
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../store/useUserStore'
import bgImg from '@/assets/images/beijingtu.png'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const formRef = ref()
const loading = ref(false)
const rememberMe = ref(false)

const form = reactive({
  username: '',
  password: '',
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

const apiHint = `${import.meta.env.VITE_API_BASE_URL || '/api'}/auth/login`

const STORAGE_KEY = 'expressway_login_remember'

function loadRemembered() {
  try {
    const saved = localStorage.getItem(STORAGE_KEY)
    if (saved) {
      const data = JSON.parse(saved)
      form.username = data.username || ''
      form.password = data.password || ''
      rememberMe.value = true
    }
  } catch (e) {
    console.error('加载记住的账号密码失败', e)
  }
}

function saveRemembered() {
  if (rememberMe.value) {
    const data = {
      username: form.username,
      password: form.password
    }
    localStorage.setItem(STORAGE_KEY, JSON.stringify(data))
  } else {
    localStorage.removeItem(STORAGE_KEY)
  }
}

onMounted(() => {
  loadRemembered()
})

function goToRegister() {
  router.push('/register')
}

async function onSubmit() {
  await formRef.value?.validate?.()
  loading.value = true
  try {
    await userStore.login({ username: form.username, password: form.password })
    saveRemembered()
    ElMessage.success('登录成功')
    const redirect = route.query.redirect || '/'
    router.replace(String(redirect))
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || e?.message || '登录失败，请检查账号密码或后端接口')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.page {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  background-color: transparent;
  position: relative;
}

.page::before {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at 20% 30%, rgba(59, 130, 246, 0.15), transparent 50%),
              radial-gradient(circle at 80% 70%, rgba(16, 185, 129, 0.12), transparent 50%);
  pointer-events: none;
}

.card {
  width: 440px;
  padding: 40px 40px 32px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 24px;
  box-shadow: 
    0 0 0 1px rgba(255, 255, 255, 0.1),
    0 32px 80px rgba(0, 0, 0, 0.15),
    0 8px 24px rgba(0, 0, 0, 0.08);
  position: relative;
  z-index: 1;
  animation: fadeInUp 0.6s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.card__header {
  text-align: center;
  margin-bottom: 28px;
}

.card__logo {
  margin-bottom: 16px;
}

.logo-icon {
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  margin: 0 auto;
  box-shadow: 0 8px 20px rgba(59, 130, 246, 0.3);
}

.card__title {
  font-size: 22px;
  font-weight: 700;
  color: #1e293b;
  letter-spacing: -0.3px;
}

.card__sub {
  margin-top: 8px;
  color: #64748b;
  font-size: 14px;
}

.form {
  margin-top: 4px;
}

.input-styled {
  margin-top: 4px;
}

.input-styled :deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.8);
  border-radius: 12px;
  padding: 10px 16px;
  box-shadow: 0 0 0 1px rgba(148, 163, 184, 0.2);
  transition: all 0.3s ease;
}

.input-styled :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px rgba(59, 130, 246, 0.5);
}

.input-styled :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.4), 0 0 0 1px rgba(59, 130, 246, 0.5);
  background: #ffffff;
}

.remember-row {
  margin: 4px 0 16px;
  display: flex;
  align-items: center;
}

.submit {
  width: 100%;
  margin-top: 8px;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 12px;
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  border: none;
  box-shadow: 0 8px 20px rgba(59, 130, 246, 0.25);
  transition: all 0.3s ease;
}

.submit:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 28px rgba(59, 130, 246, 0.35);
}

.submit:active {
  transform: translateY(0);
}

.register-hint {
  margin-top: 20px;
  text-align: center;
  font-size: 14px;
  color: #64748b;
}

.register-hint :deep(.el-link) {
  font-weight: 500;
}

.hint {
  margin-top: 24px;
  color: #94a3b8;
  font-size: 13px;
  text-align: center;
}
</style>
