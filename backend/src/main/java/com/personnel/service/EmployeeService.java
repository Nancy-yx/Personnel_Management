package com.personnel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.personnel.common.PageResult;
import com.personnel.dto.EmployeeQueryDTO;
import com.personnel.entity.Employee;

import java.util.List;
import java.util.Map;

/**
 * 员工服务接口
 */
public interface EmployeeService extends IService<Employee> {

    /**
     * 分页查询员工
     */
    PageResult<Employee> getEmployeePage(EmployeeQueryDTO queryDTO);

    /**
     * 根据ID获取员工
     */
    Employee getEmployeeById(Long id);

    /**
     * 新增员工
     */
    Employee addEmployee(Employee employee);

    /**
     * 修改员工
     */
    Employee updateEmployee(Employee employee);

    /**
     * 删除员工
     */
    void deleteEmployee(Long id);

    /**
     * 批量删除员工
     */
    void batchDelete(List<Long> ids);

    /**
     * 生成员工工号
     */
    String generateEmployeeNo();

    /**
     * 获取仪表盘数据
     */
    Map<String, Object> getDashboardData();
}
