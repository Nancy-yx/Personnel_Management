import request from '@/utils/request'

export function loginApi(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

export function getUserInfoApi() {
  return request({
    url: '/auth/userInfo',
    method: 'get'
  })
}

export function updatePasswordApi(data) {
  return request({
    url: '/auth/password',
    method: 'put',
    data
  })
}
