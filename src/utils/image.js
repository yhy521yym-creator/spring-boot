/** 将后端返回的相对路径转为浏览器可访问的 URL（开发环境走 Vite /api 代理） */
export function resolveImageUrl(url) {
  if (!url) return ''
  if (url.startsWith('http://') || url.startsWith('https://') || url.startsWith('data:')) {
    return url
  }
  if (url.startsWith('/api/uploads/')) return url
  if (url.startsWith('/uploads/')) return `/api${url}`
  if (url.startsWith('uploads/')) return `/api/${url}`
  if (url.startsWith('/api/')) return url
  if (url.startsWith('/')) return `/api${url}`
  return url
}
