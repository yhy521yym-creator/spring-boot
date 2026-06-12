import request from './request'

/**
 * 统计相关API
 */
export const getRegionStatistics = () => {
  return request({
    url: '/statistics/service-area-count-by-region',
    method: 'GET'
  })
}

export const getFacilitiesStatistics = () => {
  return request({
    url: '/statistics/facilities-distribution',
    method: 'GET'
  })
}

export const getVisitsStatistics = () => {
  return request({
    url: '/statistics/service-area-visits',
    method: 'GET'
  })
}

export const getTodayVisits = () => {
  return request({
    url: '/statistics/today-visits',
    method: 'GET'
  })
}

export const statisticsApi = {
  getOrderStatistics: () => {
    return request({
      url: '/statistics/orders',
      method: 'GET'
    })
  },
  
  getOrderTrend: (days) => {
    return request({
      url: '/statistics/orders/trend',
      method: 'GET',
      params: { days }
    })
  },
  
  getOrderStatisticsByMerchant: () => {
    return request({
      url: '/statistics/orders/merchant',
      method: 'GET'
    })
  },
  
  getOrderStatisticsByServiceArea: () => {
    return request({
      url: '/statistics/orders/service-area',
      method: 'GET'
    })
  }
}
