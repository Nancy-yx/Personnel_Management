package com.personnel.controller;

import com.personnel.common.PageResult;
import com.personnel.common.Result;
import com.personnel.dto.EmployeeQueryDTO;
import com.personnel.entity.Employee;
import com.personnel.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 员工控制器
 */
@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    /**
     * 分页查询员工列表
     */
    @GetMapping
    public Result<PageResult<Employee>> list(EmployeeQueryDTO queryDTO) {
        return Result.success(employeeService.getEmployeePage(queryDTO));
    }

    /**
     * 获取员工详情
     */
    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id) {
        return Result.success(employeeService.getEmployeeById(id));
    }

    /**
     * 新增员工
     */
    @PostMapping
    public Result<Employee> add(@RequestBody Employee employee) {
        return Result.success(employeeService.addEmployee(employee));
    }

    /**
     * 修改员工
     */
    @PutMapping("/{id}")
    public Result<Employee> update(@PathVariable Long id, @RequestBody Employee employee) {
        employee.setId(id);
        return Result.success(employeeService.updateEmployee(employee));
    }

    /**
     * 删除员工
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return Result.success();
    }

    /**
     * 批量删除员工
     */
    @DeleteMapping("/batch")
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        employeeService.batchDelete(ids);
        return Result.success();
    }

    /**
     * 生成员工工号
     */
    @GetMapping("/generateNo")
    public Result<String> generateNo() {
        return Result.success(employeeService.generateEmployeeNo());
    }
}
