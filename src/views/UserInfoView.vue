<template>
  <div class="page-view user-info-container">
    <el-row :gutter="20" class="top-cards">
      <el-col :xs="24" :md="12">
        <el-card class="info-card" shadow="never">
          <template #header>
            <div class="cardHeader">个人信息</div>
          </template>

          <el-skeleton :loading="loadingUser" animated>
            <template #default>
              <div class="profile-section">
                <div class="avatar-wrapper">
                  <el-avatar :size="100" :src="avatarUrl" class="user-avatar">
                    <el-icon><User /></el-icon>
                  </el-avatar>
                  <el-upload
                    class="avatar-uploader"
                    :show-file-list="false"
                    :before-upload="beforeAvatarUpload"
                    :http-request="handleAvatarUpload"
                    accept="image/*"
                  >
                    <div class="avatar-cover">
                    <el-icon><Camera /></el-icon>
                    </div>
                  </el-upload>
                </div>
                <div class="info-form">
                  <el-form :model="infoForm" ref="infoRef" label-width="80px" label-position="top">
                    <el-form-item label="用户名">
                      <el-input v-model="infoForm.username" disabled />
                    </el-form-item>
                    <el-form-item label="手机号" prop="phone">
                      <el-input v-model="infoForm.phone" placeholder="请输入手机号" />
                    </el-form-item>
                    <el-form-item label="角色">
                      <el-tag type="primary">{{ roleLabel || '-' }}</el-tag>
                    </el-form-item>
                    <el-form-item label="用户ID">
                      <el-input :value="user?.id || ''" disabled />
                    </el-form-item>
                    <el-form-item>
                      <el-button type="primary" @click="handleSaveInfo" :loading="savingInfo">保存修改</el-button>
                    </el-form-item>
                  </el-form>
                </div>
              </div>
            </template>
          </el-skeleton>
        </el-card>
      </el-col>

      <el-col :xs="24" :md="12">
        <el-card class="password-card" shadow="never">
          <template #header>
            <div class="cardHeader">修改密码</div>
          </template>

          <el-form ref="pwdRef" :model="pwdForm" :rules="pwdRules" label-width="80px" label-position="top">
            <el-form-item label="旧密码" prop="oldPassword">
              <el-input v-model="pwdForm.oldPassword" type="password" show-password autocomplete="current-password" placeholder="请输入旧密码" />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="pwdForm.newPassword" type="password" show-password autocomplete="new-password" placeholder="请输入新密码" />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="pwdForm.confirmPassword" type="password" show-password autocomplete="new-password" placeholder="请确认新密码" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="saving" @click="onChangePassword">保存</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" class="shortcut-card">
      <template #header>
        <div class="cardHeader">订单与预约</div>
      </template>
      <p class="shortcut-hint">查询、取消订单及查看预约记录请在专用菜单中操作，避免与个人中心功能重复。</p>
      <div class="shortcut-actions">
        <el-button type="primary" @click="router.push('/order')">前往我的订单</el-button>
        <el-button @click="router.push('/my-reservations')">前往我的预约</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Camera } from '@element-plus/icons-vue'
import { useUserStore } from '../store/useUserStore'
import request from '../api/request'

const userStore = useUserStore()
const router = useRouter()

const loadingUser = ref(false)
const saving = ref(false)
const savingInfo = ref(false)
const pwdRef = ref()
const infoRef = ref()

const user = computed(() => userStore.user)

const roleLabel = computed(() => {
  const r = userStore.role
  const map = {
    SUPER_ADMIN: '超级管理员',
    OPERATOR: '运营管理员',
    MERCHANT: '商户',
    USER: '普通用户',
  }
  return map[r] || r
})

const avatarUrl = computed(() => userStore.user?.avatar || '')

const infoForm = reactive({
  username: '',
  phone: '',
  avatar: '',
})

const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
})

const pwdRules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '新密码至少 6 位', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (_rule, value, callback) => {
        if (value !== pwdForm.newPassword) callback(new Error('两次输入的新密码不一致'))
        else callback()
      },
      trigger: 'blur',
    },
  ],
}

const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

const handleAvatarUpload = async ({ file }) => {
  try {
    const formData = new FormData()
    formData.append('file', file)
    const res = await request.post('/files/upload', formData, {
      params: { type: 'avatar' },
    })
    if (res.code === 0 || res.success || res.data) {
      const avatarUrl = res.data?.url || res.data?.fileName || res.data
      infoForm.avatar = avatarUrl
      userStore.user.avatar = avatarUrl
      ElMessage.success('头像上传成功')
    } else {
      ElMessage.error('头像上传失败')
    }
  } catch (error) {
    console.error('头像上传失败', error)
    ElMessage.error('头像上传失败')
  }
}

const handleSaveInfo = async () => {
  try {
    savingInfo.value = true
    const updateData = {
      phone: infoForm.phone,
      avatar: infoForm.avatar,
    }
    const res = await request.put('/users/me', updateData)
    if (res.code === 0) {
      ElMessage.success('信息修改成功')
      await userStore.fetchMe()
    } else {
      ElMessage.error(res.message || '修改失败')
    }
  } catch (error) {
    console.error('修改信息失败', error)
    ElMessage.error('修改失败')
  } finally {
    savingInfo.value = false
  }
}

async function onChangePassword() {
  await pwdRef.value?.validate?.()
  saving.value = true
  try {
    await userStore.changePassword({
      oldPassword: pwdForm.oldPassword,
      newPassword: pwdForm.newPassword,
    })
    ElMessage.success('密码修改成功，请重新登录')
    userStore.logout()
    window.location.href = '/login'
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || e?.message || '修改失败，请检查旧密码或后端接口')
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  if (!userStore.isLoggedIn) return
  if (!userStore.user) {
    loadingUser.value = true
    try {
      await userStore.fetchMe()
    } catch {
    } finally {
      loadingUser.value = false
    }
  }
  if (userStore.user) {
    infoForm.username = userStore.user.username || ''
    infoForm.phone = userStore.user.phone || ''
    infoForm.avatar = userStore.user.avatar || ''
  }
})
</script>

<style scoped>
.user-info-container {
  padding: 24px;
}

.top-cards {
  margin-bottom: 24px;
}

.info-card, .password-card {
  height: 100%;
}

.cardHeader {
  font-weight: 600;
  color: #0f172a;
  font-size: 16px;
}

.profile-section {
  display: flex;
  gap: 24px;
  padding: 8px 0;
}

.avatar-wrapper {
  position: relative;
  flex-shrink: 0;
}

.user-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.avatar-uploader {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.avatar-cover {
  position: absolute;
  inset: 0;
  background: rgba(0,0,0,0.4);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
  color: white;
  font-size: 32px;
}

.avatar-wrapper:hover .avatar-cover {
  opacity: 1;
}

.info-form {
  flex: 1;
}

.shortcut-card {
  margin-top: 0;
}

.shortcut-hint {
  margin: 0 0 16px;
  color: #64748b;
  font-size: 14px;
  line-height: 1.6;
}

.shortcut-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}
</style>
