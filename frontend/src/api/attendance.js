import request from '@/utils/request'

export function getAttendanceList(params) {
  return request({
    url: '/attendances',
    method: 'get',
    params
  })
}

export function getAttendanceById(id) {
  return request({
    url: `/attendances/${id}`,
    method: 'get'
  })
}

export function addAttendance(data) {
  return request({
    url: '/attendances',
    method: 'post',
    data
  })
}

export function updateAttendance(id, data) {
  return request({
    url: `/attendances/${id}`,
    method: 'put',
    data
  })
}

export function deleteAttendance(id) {
  return request({
    url: `/attendances/${id}`,
    method: 'delete'
  })
}

export function batchDeleteAttendances(ids) {
  return request({
    url: '/attendances/batch',
    method: 'delete',
    data: ids
  })
}

export function supplementaryAttendance(data) {
  return request({
    url: '/attendances/supplementary',
    method: 'post',
    data
  })
}

export function getAttendanceStats(startDate, endDate) {
  return request({
    url: '/attendances/stats',
    method: 'get',
    params: { startDate, endDate }
  })
}

export function getAbnormalRecords(limit = 10) {
  return request({
    url: '/attendances/abnormal',
    method: 'get',
    params: { limit }
  })
}

export function generateMonthlySummary(month) {
  return request({
    url: `/attendances/summary/${month}`,
    method: 'get'
  })
}
