<template>
  <div class="page" :style="{ backgroundImage: `url(${bgImg})` }">
    <div class="card">
      <div class="card__header">
        <div class="card__logo">
          <div class="logo-icon">🚀</div>
        </div>
        <div class="card__title">高速服务区服务管理系统</div>
        <div class="card__sub">请填写信息注册账号</div>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" size="large" class="form">
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名"
            :prefix-icon="User"
            clearable
            class="input-styled"
          />
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input
            v-model="form.email"
            placeholder="请输入邮箱"
            :prefix-icon="Message"
            clearable
            class="input-styled"
          />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            :type="showPassword ? 'text' : 'password'"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            class="input-styled"
          >
            <template #suffix>
              <el-icon class="password-toggle" @click="showPassword = !showPassword">
                <component :is="showPassword ? Hide : View" />
              </el-icon>
            </template>
          </el-input>
          <div class="password-strength" v-if="form.password">
            <div class="strength-bar">
              <div
                class="strength-level"
                :class="passwordStrength.level"
                :style="{ width: passwordStrength.width }"
              ></div>
            </div>
            <span class="strength-text" :class="passwordStrength.level">
              {{ passwordStrength.text }}
            </span>
          </div>
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            :type="showConfirmPassword ? 'text' : 'password'"
            placeholder="请再次输入密码"
            :prefix-icon="Lock"
            class="input-styled"
          >
            <template #suffix>
              <el-icon class="password-toggle" @click="showConfirmPassword = !showConfirmPassword">
                <component :is="showConfirmPassword ? Hide : View" />
              </el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-button type="primary" class="submit" :loading="loading" @click="handleRegister">
          <span v-if="!loading">注 册</span>
          <span v-else>注册中...</span>
        </el-button>

        <div class="login-hint">
          已有账号？<el-link type="primary" @click="goToLogin">立即登录</el-link>
        </div>

        <div class="hint">
          高速服务区智能管理平台 © 2026
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { User, Lock, View, Hide, Message } from '@element-plus/icons-vue'
import { useUserStore } from '../store/useUserStore'
import bgImg from '@/assets/images/beijingtu.png'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)
const showPassword = ref(false)
const showConfirmPassword = ref(false)

const form = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请确认密码'))
  } else if (value !== form.password) {
    callback(new Error('两次密码输入不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度为2-20个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const passwordStrength = computed(() => {
  const password = form.password
  if (!password) return { level: '', text: '', width: '0%' }

  let strength = 0
  if (password.length >= 6) strength++
  if (password.length >= 8) strength++
  if (/[a-z]/.test(password) && /[A-Z]/.test(password)) strength++
  if (/\d/.test(password)) strength++
  if (/[!@#$%^&*(),.?":{}|<>]/.test(password)) strength++

  if (strength <= 2) {
    return { level: 'weak', text: '弱', width: '33%' }
  } else if (strength <= 3) {
    return { level: 'medium', text: '中', width: '66%' }
  } else {
    return { level: 'strong', text: '强', width: '100%' }
  }
})

function goToLogin() {
  router.push('/login')
}

async function handleRegister() {
  try {
    await formRef.value?.validate()
  } catch {
    return
  }

  loading.value = true
  try {
    const res = await userStore.register({
      username: form.username,
      password: form.password,
      email: form.email
    })

    if (res.code === 0) {
      ElMessage.success('注册成功')
      router.push('/login')
    } else {
      ElMessage.error(res.message || '注册失败')
    }
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || error?.message || '注册失败')
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

.password-strength {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 8px;
}

.strength-bar {
  flex: 1;
  height: 4px;
  background: #e2e8f0;
  border-radius: 2px;
  overflow: hidden;
}

.strength-level {
  height: 100%;
  border-radius: 2px;
  transition: all 0.3s ease;
}

.strength-level.weak {
  background: #f56565;
}

.strength-level.medium {
  background: #f6ad55;
}

.strength-level.strong {
  background: #48bb78;
}

.strength-text {
  font-size: 12px;
  font-weight: 500;
  min-width: 24px;
}

.strength-text.weak {
  color: #f56565;
}

.strength-text.medium {
  color: #f6ad55;
}

.strength-text.strong {
  color: #48bb78;
}

.password-toggle {
  cursor: pointer;
  color: #94a3b8;
  transition: color 0.2s;
  font-size: 18px;
}

.password-toggle:hover {
  color: #3b82f6;
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

.login-hint {
  margin-top: 20px;
  text-align: center;
  font-size: 14px;
  color: #64748b;
}

.login-hint :deep(.el-link) {
  font-weight: 500;
}

.hint {
  margin-top: 24px;
  color: #94a3b8;
  font-size: 13px;
  text-align: center;
}
</style>
