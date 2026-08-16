import request from '@/utils/request'

export function getDepartmentTree() {
  return request({
    url: '/departments/tree',
    method: 'get'
  })
}

export function getDepartmentList(params) {
  return request({
    url: '/departments/list',
    method: 'get',
    params
  })
}

export function getAllDepartments() {
  return request({
    url: '/departments/all',
    method: 'get'
  })
}

export function addDepartment(data) {
  return request({
    url: '/departments',
    method: 'post',
    data
  })
}

export function updateDepartment(id, data) {
  return request({
    url: `/departments/${id}`,
    method: 'put',
    data
  })
}

export function deleteDepartment(id) {
  return request({
    url: `/departments/${id}`,
    method: 'delete'
  })
}

export function getDepartmentEmployees(id) {
  return request({
    url: `/departments/${id}/employees`,
    method: 'get'
  })
}
