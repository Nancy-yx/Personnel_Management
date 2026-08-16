import request from '@/utils/request'

export function getEmployeeList(params) {
  return request({
    url: '/employees',
    method: 'get',
    params
  })
}

export function getEmployeeById(id) {
  return request({
    url: `/employees/${id}`,
    method: 'get'
  })
}

export function addEmployee(data) {
  return request({
    url: '/employees',
    method: 'post',
    data
  })
}

export function updateEmployee(id, data) {
  return request({
    url: `/employees/${id}`,
    method: 'put',
    data
  })
}

export function deleteEmployee(id) {
  return request({
    url: `/employees/${id}`,
    method: 'delete'
  })
}

export function batchDeleteEmployees(ids) {
  return request({
    url: '/employees/batch',
    method: 'delete',
    data: ids
  })
}

export function generateEmployeeNo() {
  return request({
    url: '/employees/generateNo',
    method: 'get'
  })
}
