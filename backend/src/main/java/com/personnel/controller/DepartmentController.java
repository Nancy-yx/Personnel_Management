package com.personnel.controller;

import com.personnel.common.Result;
import com.personnel.entity.Department;
import com.personnel.entity.Employee;
import com.personnel.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 部门控制器
 */
@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    /**
     * 获取部门树形结构
     */
    @GetMapping("/tree")
    public Result<List<Map<String, Object>>> getTree() {
        return Result.success(departmentService.getDepartmentTree());
    }

    /**
     * 获取部门列表
     */
    @GetMapping("/list")
    public Result<List<Department>> getList() {
        return Result.success(departmentService.getList());
    }

    /**
     * 获取所有启用的部门
     */
    @GetMapping("/all")
    public Result<List<Department>> getAll() {
        return Result.success(departmentService.getAll());
    }

    /**
     * 新增部门
     */
    @PostMapping
    public Result<Department> add(@RequestBody Department department) {
        return Result.success(departmentService.addDepartment(department));
    }

    /**
     * 修改部门
     */
    @PutMapping("/{id}")
    public Result<Department> update(@PathVariable Long id, @RequestBody Department department) {
        department.setId(id);
        return Result.success(departmentService.updateDepartment(department));
    }

    /**
     * 删除部门
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return Result.success();
    }

    /**
     * 获取部门员工
     */
    @GetMapping("/{id}/employees")
    public Result<List<Employee>> getEmployees(@PathVariable Long id) {
        return Result.success(departmentService.getDepartmentEmployees(id));
    }
}
