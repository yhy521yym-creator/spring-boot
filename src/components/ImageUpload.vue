<template>
  <div class="image-upload">
    <div class="uploader-wrap">
      <el-upload
        class="uploader"
        :show-file-list="false"
        :http-request="handleUpload"
        :before-upload="beforeUpload"
        accept="image/*"
      >
        <img v-if="modelValue" :src="resolveImageUrl(modelValue)" class="preview" alt="" />
        <el-icon v-else class="placeholder"><Plus /></el-icon>
      </el-upload>
      <el-button
        v-if="modelValue && allowDelete"
        class="delete-btn"
        type="danger"
        :icon="Delete"
        circle
        size="small"
        title="删除图片"
        :loading="removing"
        @click.stop="handleRemove"
      />
    </div>
    <div v-if="tip" class="tip">{{ tip }}</div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Plus, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { fileApi } from '../api/file'
import { resolveImageUrl } from '../utils/image'

const props = defineProps({
  modelValue: { type: String, default: '' },
  type: { type: String, default: 'image' },
  tip: { type: String, default: '点击上传，不超过 2MB；可删除后重传' },
  allowDelete: { type: Boolean, default: true },
})

const emit = defineEmits(['update:modelValue'])

const removing = ref(false)

const beforeUpload = (file) => {
  if (!file.type.startsWith('image/')) {
    ElMessage.error('只能上传图片')
    return false
  }
  if (file.size / 1024 / 1024 > 2) {
    ElMessage.error('图片不能超过 2MB')
    return false
  }
  return true
}

const deleteRemoteFile = async (url) => {
  if (!url || url.startsWith('data:')) return
  try {
    await fileApi.remove(url)
  } catch {
    // 文件可能已被删或仅存于数据库，清空绑定即可
  }
}

const handleUpload = async ({ file }) => {
  try {
    const oldUrl = props.modelValue
    const res = await fileApi.upload(file, props.type)
    if (res.code === 0 && res.data?.url) {
      emit('update:modelValue', res.data.url)
      ElMessage.success('上传成功')
      if (oldUrl && oldUrl !== res.data.url) {
        await deleteRemoteFile(oldUrl)
      }
    } else {
      ElMessage.error(res.message || '上传失败')
    }
  } catch {
    ElMessage.error('上传失败')
  }
}

const handleRemove = async () => {
  try {
    await ElMessageBox.confirm('确定删除当前图片吗？删除后需点击保存才会更新到记录。', '删除图片', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning',
    })
  } catch {
    return
  }

  removing.value = true
  try {
    await deleteRemoteFile(props.modelValue)
    emit('update:modelValue', '')
    ElMessage.success('图片已删除')
  } finally {
    removing.value = false
  }
}
</script>

<style scoped>
.image-upload {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.uploader-wrap {
  position: relative;
  display: inline-block;
}

.uploader :deep(.el-upload) {
  border: 1px dashed #dcdfe6;
  border-radius: 8px;
  cursor: pointer;
  width: 120px;
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.preview {
  width: 120px;
  height: 120px;
  object-fit: cover;
}

.placeholder {
  font-size: 28px;
  color: #8c939d;
}

.delete-btn {
  position: absolute;
  top: 4px;
  right: 4px;
  z-index: 2;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.15);
}

.tip {
  font-size: 12px;
  color: #909399;
  max-width: 280px;
  line-height: 1.4;
}
</style>
