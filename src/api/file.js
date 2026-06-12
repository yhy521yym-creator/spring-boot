import request from './request'

export const fileApi = {
  upload(file, type = 'image') {
    const formData = new FormData()
    formData.append('file', file)
    // 勿手动设置 Content-Type，否则 multipart 缺少 boundary 会导致上传失败
    return request.post('/files/upload', formData, {
      params: { type },
    })
  },
  remove(url) {
    return request.delete('/files', { params: { url } })
  },
}
