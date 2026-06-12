/** 解析服务区 facilities 字段（JSON 或逗号分隔） */
export function parseFacilities(facilities) {
  if (!facilities) return []
  if (Array.isArray(facilities)) return facilities.filter(Boolean)
  const str = String(facilities).trim()
  if (!str) return []
  if (str.startsWith('[')) {
    try {
      const list = JSON.parse(str)
      return Array.isArray(list) ? list.filter(Boolean) : []
    } catch {
      // fall through
    }
  }
  return str.split(',').map((s) => s.trim()).filter(Boolean)
}
