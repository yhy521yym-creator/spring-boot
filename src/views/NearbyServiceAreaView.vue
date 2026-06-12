<template>
  <div class="page-view nearby-page">
    <div class="page-head">
      <div class="page-head__main">
        <h1 class="page-head__title">附近服务区</h1>
        <p class="page-head__desc">地图选点或一键定位，查看周边服务区设施并预约</p>
      </div>
      <div class="page-head__extra">
        <el-tag v-if="hasSearched" type="info" effect="plain">
          半径 {{ radiusLabel }} · 共 {{ resultList.length }} 个
        </el-tag>
      </div>
    </div>

    <el-row :gutter="16" class="nearby-layout">
      <!-- 左侧 -->
      <el-col :xs="24" :lg="8">
        <el-card shadow="never" class="page-card side-card">
          <template #header>
            <span class="page-title-inline">查询条件</span>
          </template>

          <div class="quick-actions">
            <el-button type="primary" plain size="small" :loading="locating" @click="locateMe">
              <el-icon><Aim /></el-icon>
              定位我的位置
            </el-button>
            <el-button size="small" @click="resetSearch">重置</el-button>
          </div>

          <div class="city-chips">
            <span class="chips-label">快捷城市</span>
            <div class="chips-wrap">
              <el-tag
                v-for="c in quickCities"
                :key="c.name"
                class="city-tag"
                :effect="activeCity === c.name ? 'dark' : 'plain'"
                @click="applyCity(c)"
              >
                {{ c.name }}
              </el-tag>
            </div>
          </div>

          <el-form :model="searchForm" label-position="top" class="search-form">
            <el-row :gutter="8">
              <el-col :span="12">
                <el-form-item label="经度">
                  <el-input-number
                    v-model="searchForm.lng"
                    :precision="6"
                    :min="-180"
                    :max="180"
                    :controls="false"
                    style="width: 100%"
                    placeholder="lng"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="纬度">
                  <el-input-number
                    v-model="searchForm.lat"
                    :precision="6"
                    :min="-90"
                    :max="90"
                    :controls="false"
                    style="width: 100%"
                    placeholder="lat"
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item label="搜索半径">
              <el-radio-group v-model="searchForm.radius" class="radius-group" @change="onRadiusChange">
                <el-radio-button :value="5000">5km</el-radio-button>
                <el-radio-button :value="10000">10km</el-radio-button>
                <el-radio-button :value="20000">20km</el-radio-button>
                <el-radio-button :value="50000">50km</el-radio-button>
                <el-radio-button :value="100000">100km</el-radio-button>
              </el-radio-group>
            </el-form-item>

            <el-form-item v-if="allFacilities.length" label="设施筛选">
              <el-select
                v-model="facilityFilter"
                multiple
                collapse-tags
                collapse-tags-tooltip
                placeholder="全部设施"
                clearable
                style="width: 100%"
              >
                <el-option v-for="f in allFacilities" :key="f" :label="f" :value="f" />
              </el-select>
            </el-form-item>

            <el-button
              type="primary"
              class="search-btn"
              :loading="loading"
              :disabled="!searchForm.lng || !searchForm.lat"
              @click="handleSearch"
            >
              <el-icon><Search /></el-icon>
              查询附近服务区
            </el-button>
          </el-form>

          <el-alert
            v-if="!hasSearched"
            class="usage-tip"
            type="info"
            :closable="false"
            show-icon
          >
            <template #title>使用提示</template>
            <ul class="tip-list">
              <li>点击右侧地图任意位置可自动查询</li>
              <li>或使用「定位我的位置」获取当前坐标</li>
              <li>支持按设施类型筛选结果</li>
            </ul>
          </el-alert>

          <div v-if="hasSearched" class="result-section">
            <div class="result-toolbar">
              <span class="result-title">
                结果
                <template v-if="facilityFilter.length">（筛选后 {{ filteredList.length }}）</template>
                <template v-else>（{{ resultList.length }}）</template>
              </span>
              <el-text v-if="nearestItem" type="primary" size="small">
                最近：{{ formatDistance(nearestItem.distance) }}
              </el-text>
            </div>

            <el-scrollbar v-if="filteredList.length" max-height="320px">
              <div
                v-for="(item, index) in filteredList"
                :key="item.id"
                class="result-item"
                :class="{ active: activeId === item.id }"
                @click="focusOnMap(item)"
              >
                <div class="result-item-top">
                  <span class="result-badge">{{ index + 1 }}</span>
                  <span class="result-name">{{ item.name }}</span>
                  <span class="result-distance">{{ formatDistance(item.distance) }}</span>
                </div>
                <div class="result-address">{{ item.address || '暂无地址' }}</div>
                <div v-if="parseFacilities(item.facilities).length" class="result-tags">
                  <el-tag
                    v-for="tag in parseFacilities(item.facilities).slice(0, 4)"
                    :key="tag"
                    size="small"
                    type="info"
                    effect="plain"
                  >
                    {{ tag }}
                  </el-tag>
                  <span
                    v-if="parseFacilities(item.facilities).length > 4"
                    class="more-tags"
                  >+{{ parseFacilities(item.facilities).length - 4 }}</span>
                </div>
                <div class="result-actions">
                  <el-button type="primary" link size="small" @click.stop="openDetail(item)">详情</el-button>
                  <el-button type="success" link size="small" @click.stop="goBook(item)">去预约</el-button>
                </div>
              </div>
            </el-scrollbar>

            <el-empty
              v-else
              :description="resultList.length ? '没有符合设施筛选的服务区' : '该范围内暂无服务区'"
              :image-size="72"
            />
          </div>
        </el-card>
      </el-col>

      <!-- 右侧地图 -->
      <el-col :xs="24" :lg="16">
        <el-card shadow="never" class="page-card map-card">
          <template #header>
            <div class="map-header">
              <span class="page-title-inline">地图</span>
              <span v-if="searchForm.lng && searchForm.lat" class="coord-text">
                中心点 {{ searchForm.lng?.toFixed(4) }}, {{ searchForm.lat?.toFixed(4) }}
              </span>
            </div>
          </template>

          <div v-if="mapError" class="map-placeholder">
            <el-result icon="warning" title="地图加载失败" sub-title="请检查网络或稍后重试" />
          </div>
          <div v-else-if="!mapLoaded" class="map-placeholder">
            <el-icon class="is-loading spin"><Loading /></el-icon>
            <span>地图加载中…</span>
          </div>
          <div id="mapContainer" class="map-container" />
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="detailVisible" title="服务区详情" width="520px" destroy-on-close>
      <template v-if="selectedItem">
        <div class="detail-hero">
          <h3>{{ selectedItem.name }}</h3>
          <el-tag :type="selectedItem.status === 1 ? 'success' : 'danger'" size="small">
            {{ selectedItem.status === 1 ? '营业中' : '已停用' }}
          </el-tag>
          <span v-if="selectedItem.distance != null" class="detail-distance">
            距您 {{ formatDistance(selectedItem.distance) }}
          </span>
        </div>

        <el-descriptions :column="1" border class="detail-desc">
          <el-descriptions-item label="地址">{{ selectedItem.address || '-' }}</el-descriptions-item>
          <el-descriptions-item v-if="selectedItem.region" label="区域">
            {{ selectedItem.region }}
          </el-descriptions-item>
          <el-descriptions-item label="设施">
            <template v-if="parseFacilities(selectedItem.facilities).length">
              <el-tag
                v-for="f in parseFacilities(selectedItem.facilities)"
                :key="f"
                size="small"
                class="facility-tag"
              >
                {{ f }}
              </el-tag>
            </template>
            <span v-else class="text-muted">暂无设施信息</span>
          </el-descriptions-item>
          <el-descriptions-item label="简介">
            {{ selectedItem.description || '暂无描述' }}
          </el-descriptions-item>
        </el-descriptions>
      </template>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button type="primary" @click="goBook(selectedItem)">立即预约</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, Loading, Aim } from '@element-plus/icons-vue'
import { getNearbyServiceAreas, getServiceAreaById } from '../api/serviceArea'
import { parseFacilities } from '../utils/facilities'

const router = useRouter()

const AMAP_KEY = '5cd214e7d4714256f39034d2b17333d8'
const AMAP_SECURITY_CODE = 'bd4d3875291575e3fda15a39f0eb2d68'

const quickCities = [
  { name: '杭州', lng: 120.1551, lat: 30.2741 },
  { name: '上海', lng: 121.4737, lat: 31.2304 },
  { name: '苏州', lng: 120.5853, lat: 31.2989 },
  { name: '无锡', lng: 120.3119, lat: 31.4912 },
  { name: '南京', lng: 118.7969, lat: 32.0603 },
]

let map = null
let markers = []
let userMarker = null
let radiusCircle = null

const mapLoaded = ref(false)
const mapError = ref(false)
const locating = ref(false)
const activeCity = ref('')
const activeId = ref(null)

const searchForm = reactive({
  lng: null,
  lat: null,
  radius: 50000,
})

const loading = ref(false)
const hasSearched = ref(false)
const resultList = ref([])
const facilityFilter = ref([])
const selectedItem = ref(null)
const detailVisible = ref(false)

const radiusLabel = computed(() => {
  const r = searchForm.radius
  if (r >= 1000) return `${r / 1000}km`
  return `${r}m`
})

const filteredList = computed(() => {
  if (!facilityFilter.value.length) return resultList.value
  return resultList.value.filter((item) => {
    const tags = parseFacilities(item.facilities)
    return facilityFilter.value.every((f) => tags.includes(f))
  })
})

const allFacilities = computed(() => {
  const set = new Set()
  resultList.value.forEach((item) => {
    parseFacilities(item.facilities).forEach((f) => set.add(f))
  })
  return [...set].sort()
})

const nearestItem = computed(() => filteredList.value[0] || null)

const formatDistance = (distance) => {
  if (distance == null) return '-'
  if (distance < 1000) return `${Math.round(distance)} 米`
  return `${(distance / 1000).toFixed(2)} 公里`
}

const clearMapOverlays = () => {
  if (!map) return
  markers.forEach((m) => map.remove(m))
  markers = []
  if (userMarker) {
    map.remove(userMarker)
    userMarker = null
  }
  if (radiusCircle) {
    map.remove(radiusCircle)
    radiusCircle = null
  }
}

const renderMapOverlays = () => {
  if (!map || !mapLoaded.value || !searchForm.lng || !searchForm.lat) return
  const AMap = window.AMap
  clearMapOverlays()

  const center = [searchForm.lng, searchForm.lat]

  userMarker = new AMap.Marker({
    position: center,
    title: '查询位置',
    anchor: 'bottom-center',
  })
  map.add(userMarker)

  radiusCircle = new AMap.Circle({
    center,
    radius: searchForm.radius,
    strokeColor: '#3b82f6',
    strokeWeight: 2,
    strokeOpacity: 0.8,
    fillColor: '#3b82f6',
    fillOpacity: 0.12,
  })
  map.add(radiusCircle)

  filteredList.value.forEach((item, index) => {
    if (!item.longitude || !item.latitude) return
    const pos = [Number(item.longitude), Number(item.latitude)]
    const marker = new AMap.Marker({
      position: pos,
      title: item.name,
      label: {
        content: `<span class="amap-num">${index + 1}</span>`,
        direction: 'top',
      },
    })
    marker.on('click', () => {
      activeId.value = item.id
      openDetail(item)
    })
    map.add(marker)
    markers.push(marker)
  })

  const fitTargets = [userMarker, radiusCircle, ...markers]
  if (fitTargets.length) {
    map.setFitView(fitTargets, false, [48, 48, 48, 48])
  } else {
    map.setCenter(center)
    map.setZoom(11)
  }
}

const initMap = () => {
  window._AMapSecurityConfig = { securityJsCode: AMAP_SECURITY_CODE }

  const callbackName = `AMapInitCallback_${Date.now()}`
  window[callbackName] = () => {
    if (typeof window.AMap === 'undefined') {
      mapError.value = true
      delete window[callbackName]
      return
    }
    try {
      map = new window.AMap.Map('mapContainer', {
        zoom: 10,
        center: [120.15, 30.27],
        viewMode: '2D',
      })
      map.on('click', (e) => {
        searchForm.lng = parseFloat(e.lnglat.getLng().toFixed(6))
        searchForm.lat = parseFloat(e.lnglat.getLat().toFixed(6))
        activeCity.value = ''
        handleSearch()
      })
      mapLoaded.value = true
      delete window[callbackName]
    } catch {
      mapError.value = true
      delete window[callbackName]
    }
  }

  const script = document.createElement('script')
  script.src = `https://webapi.amap.com/maps?v=2.0&key=${AMAP_KEY}&callback=${callbackName}`
  script.async = true
  script.onerror = () => {
    mapError.value = true
    delete window[callbackName]
  }
  document.head.appendChild(script)
}

const handleSearch = async () => {
  if (searchForm.lng == null || searchForm.lat == null) {
    ElMessage.warning('请先选择或输入坐标')
    return
  }

  loading.value = true
  hasSearched.value = true
  facilityFilter.value = []

  try {
    const res = await getNearbyServiceAreas({
      lat: searchForm.lat,
      lng: searchForm.lng,
      radius: searchForm.radius,
    })

    if (res.code === 0) {
      resultList.value = res.data || []
      activeId.value = resultList.value[0]?.id ?? null
      if (resultList.value.length) {
        ElMessage.success(`找到 ${resultList.value.length} 个服务区`)
      } else {
        ElMessage.info('该范围内暂无服务区，可扩大搜索半径')
      }
      renderMapOverlays()
    } else {
      ElMessage.error(res.message || '查询失败')
    }
  } catch {
    ElMessage.error('查询失败')
  } finally {
    loading.value = false
  }
}

const onRadiusChange = () => {
  if (hasSearched.value && searchForm.lng != null) {
    handleSearch()
  } else if (map && searchForm.lng != null) {
    renderMapOverlays()
  }
}

const locateMe = () => {
  if (!navigator.geolocation) {
    ElMessage.warning('当前浏览器不支持定位')
    return
  }
  locating.value = true
  navigator.geolocation.getCurrentPosition(
    (pos) => {
      searchForm.lng = parseFloat(pos.coords.longitude.toFixed(6))
      searchForm.lat = parseFloat(pos.coords.latitude.toFixed(6))
      activeCity.value = ''
      locating.value = false
      ElMessage.success('定位成功')
      handleSearch()
    },
    () => {
      locating.value = false
      ElMessage.error('定位失败，请检查浏览器定位权限')
    },
    { enableHighAccuracy: true, timeout: 10000 }
  )
}

const applyCity = (city) => {
  activeCity.value = city.name
  searchForm.lng = city.lng
  searchForm.lat = city.lat
  if (map) {
    map.setCenter([city.lng, city.lat])
    map.setZoom(10)
  }
  handleSearch()
}

const resetSearch = () => {
  searchForm.lng = null
  searchForm.lat = null
  searchForm.radius = 50000
  activeCity.value = ''
  activeId.value = null
  hasSearched.value = false
  resultList.value = []
  facilityFilter.value = []
  clearMapOverlays()
  if (map) {
    map.setCenter([120.15, 30.27])
    map.setZoom(10)
  }
}

const focusOnMap = (item) => {
  activeId.value = item.id
  if (map && item.longitude && item.latitude) {
    map.setCenter([Number(item.longitude), Number(item.latitude)])
    map.setZoom(13)
  }
}

const openDetail = async (item) => {
  activeId.value = item.id
  try {
    const res = await getServiceAreaById(item.id)
    if (res.code === 0) {
      selectedItem.value = { ...res.data, distance: item.distance }
    } else {
      selectedItem.value = item
    }
  } catch {
    selectedItem.value = item
  }
  detailVisible.value = true
}

const goBook = (item) => {
  const id = item?.id
  detailVisible.value = false
  if (id) {
    router.push({ path: '/book', query: { serviceAreaId: id } })
  } else {
    router.push('/book')
  }
}

watch(facilityFilter, () => {
  if (hasSearched.value) renderMapOverlays()
})

onMounted(() => {
  initMap()
})

onUnmounted(() => {
  clearMapOverlays()
  if (map) {
    map.destroy()
    map = null
  }
})
</script>

<style scoped>
.nearby-layout {
  align-items: stretch;
}

.side-card :deep(.el-card__body) {
  padding: 16px 18px 20px;
}

.quick-actions {
  display: flex;
  gap: 8px;
  margin-bottom: 14px;
}

.city-chips {
  margin-bottom: 16px;
}

.chips-label {
  font-size: 12px;
  color: #64748b;
  display: block;
  margin-bottom: 8px;
}

.chips-wrap {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.city-tag {
  cursor: pointer;
  user-select: none;
}

.search-form {
  margin-bottom: 8px;
}

.radius-group {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.radius-group :deep(.el-radio-button__inner) {
  padding: 8px 10px;
}

.search-btn {
  width: 100%;
  margin-top: 4px;
}

.usage-tip {
  margin-top: 12px;
}

.tip-list {
  margin: 6px 0 0;
  padding-left: 18px;
  font-size: 12px;
  line-height: 1.7;
  color: #64748b;
}

.result-section {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #f1f5f9;
}

.result-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.result-title {
  font-weight: 600;
  font-size: 14px;
  color: #1e293b;
}

.result-item {
  padding: 12px 14px;
  margin-bottom: 10px;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  cursor: pointer;
  transition: border-color 0.2s, box-shadow 0.2s, background 0.2s;
  background: #fff;
}

.result-item:hover {
  border-color: #93c5fd;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.12);
}

.result-item.active {
  border-color: #3b82f6;
  background: linear-gradient(135deg, #eff6ff 0%, #fff 100%);
}

.result-item-top {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}

.result-badge {
  width: 22px;
  height: 22px;
  border-radius: 6px;
  background: #3b82f6;
  color: #fff;
  font-size: 12px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.result-name {
  flex: 1;
  font-weight: 600;
  font-size: 14px;
  color: #1e293b;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.result-distance {
  color: #ef4444;
  font-weight: 600;
  font-size: 13px;
  flex-shrink: 0;
}

.result-address {
  font-size: 12px;
  color: #64748b;
  margin-bottom: 8px;
  line-height: 1.5;
}

.result-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  margin-bottom: 8px;
}

.more-tags {
  font-size: 11px;
  color: #94a3b8;
  align-self: center;
}

.result-actions {
  display: flex;
  gap: 4px;
}

.map-card :deep(.el-card__body) {
  padding: 0;
  position: relative;
}

.map-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
  width: 100%;
}

.coord-text {
  font-size: 12px;
  color: #64748b;
  font-family: ui-monospace, monospace;
}

.map-container {
  width: 100%;
  height: min(72vh, 640px);
  min-height: 420px;
}

.map-placeholder {
  height: min(72vh, 640px);
  min-height: 420px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  color: #64748b;
}

.spin {
  font-size: 28px;
}

.detail-hero {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px;
  margin-bottom: 16px;
}

.detail-hero h3 {
  margin: 0;
  font-size: 18px;
  flex: 1;
  min-width: 120px;
}

.detail-distance {
  font-size: 13px;
  color: #3b82f6;
  font-weight: 500;
}

.detail-desc {
  margin-top: 8px;
}

.facility-tag {
  margin: 2px 4px 2px 0;
}

@media (max-width: 992px) {
  .map-container,
  .map-placeholder {
    height: 400px;
    min-height: 320px;
  }
}
</style>

<style>
/* 高德地图标注序号 */
.amap-num {
  display: inline-block;
  min-width: 18px;
  padding: 0 4px;
  height: 18px;
  line-height: 18px;
  text-align: center;
  background: #3b82f6;
  color: #fff;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 600;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.2);
}
</style>
