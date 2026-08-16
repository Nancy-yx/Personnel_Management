import request from '@/utils/request'

export function getSalaryList(params) {
  return request({
    url: '/salaries',
    method: 'get',
    params
  })
}

export function getSalaryById(id) {
  return request({
    url: `/salaries/${id}`,
    method: 'get'
  })
}

export function addSalary(data) {
  return request({
    url: '/salaries',
    method: 'post',
    data
  })
}

export function updateSalary(id, data) {
  return request({
    url: `/salaries/${id}`,
    method: 'put',
    data
  })
}

export function deleteSalary(id) {
  return request({
    url: `/salaries/${id}`,
    method: 'delete'
  })
}

export function batchDeleteSalaries(ids) {
  return request({
    url: '/salaries/batch',
    method: 'delete',
    data: ids
  })
}

export function calculateMonthlySalary(month) {
  return request({
    url: `/salaries/calculate/${month}`,
    method: 'post'
  })
}

export function getTotalSalaryByMonth(month) {
  return request({
    url: `/salaries/total/${month}`,
    method: 'get'
  })
}

export function getSalaryTrend(months = 6) {
  return request({
    url: '/salaries/trend',
    method: 'get',
    params: { months }
  })
}

export function getSalaryByDepartment(month) {
  return request({
    url: '/salaries/byDepartment',
    method: 'get',
    params: { month }
  })
}
