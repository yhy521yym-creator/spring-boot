import request from './request'

/** 用户预约下单：订单与预约同一事务 */
export const bookingApi = {
  create(data) {
    return request.post('/bookings', data)
  },
}
