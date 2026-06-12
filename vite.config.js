import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

// 说明：
// - 默认将前端请求的 /api 代理到后端 http://localhost:8080
// - 如果你后端地址不同，可修改 target 或者改用 .env 配置 VITE_API_BASE_URL
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src')
    }
  },
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:9093',
        changeOrigin: true,
        // 接口去掉 /api 前缀；静态图片 /api/uploads 保留前缀以匹配后端资源映射
        rewrite: (path) => {
          if (path.startsWith('/api/uploads')) return path
          return path.replace(/^\/api/, '')
        },
      },
    },
  },
})

