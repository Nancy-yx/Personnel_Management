import request from '@/utils/request'

export function getOverview() {
  return request({
    url: '/dashboard/overview',
    method: 'get'
  })
}

export function getStats(params) {
  return request({
    url: '/dashboard/stats',
    method: 'get',
    params
  })
}
