package com.personnel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.personnel.entity.Department;
import com.personnel.entity.Employee;

import java.util.List;
import java.util.Map;

/**
 * 部门服务接口
 */
public interface DepartmentService extends IService<Department> {

    /**
     * 获取部门树形结构
     */
    List<Map<String, Object>> getDepartmentTree();

    /**
     * 获取部门列表
     */
    List<Department> getList();

    /**
     * 获取所有部门
     */
    List<Department> getAll();

    /**
     * 新增部门
     */
    Department addDepartment(Department department);

    /**
     * 修改部门
     */
    Department updateDepartment(Department department);

    /**
     * 删除部门
     */
    void deleteDepartment(Long id);

    /**
     * 获取部门员工
     */
    List<Employee> getDepartmentEmployees(Long id);
}
