package com.personnel.controller;

import com.personnel.common.PageResult;
import com.personnel.common.Result;
import com.personnel.dto.SalaryQueryDTO;
import com.personnel.entity.Salary;
import com.personnel.service.SalaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 薪资控制器
 */
@RestController
@RequestMapping("/api/salaries")
@RequiredArgsConstructor
public class SalaryController {

    private final SalaryService salaryService;

    /**
     * 分页查询薪资列表
     */
    @GetMapping
    public Result<PageResult<Salary>> list(SalaryQueryDTO queryDTO) {
        return Result.success(salaryService.getSalaryPage(queryDTO));
    }

    /**
     * 获取薪资详情
     */
    @GetMapping("/{id}")
    public Result<Salary> getById(@PathVariable Long id) {
        return Result.success(salaryService.getSalaryById(id));
    }

    /**
     * 新增薪资
     */
    @PostMapping
    public Result<Salary> add(@RequestBody Salary salary) {
        return Result.success(salaryService.addSalary(salary));
    }

    /**
     * 修改薪资
     */
    @PutMapping("/{id}")
    public Result<Salary> update(@PathVariable Long id, @RequestBody Salary salary) {
        salary.setId(id);
        return Result.success(salaryService.updateSalary(salary));
    }

    /**
     * 删除薪资
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        salaryService.deleteSalary(id);
        return Result.success();
    }

    /**
     * 核算月度薪资
     */
    @PostMapping("/calculate/{month}")
    public Result<Void> calculate(@PathVariable String month) {
        salaryService.calculateMonthlySalary(month);
        return Result.success();
    }

    /**
     * 薪资趋势
     */
    @GetMapping("/trend")
    public Result<Map<String, Object>> trend(@RequestParam(defaultValue = "6") Integer months) {
        return Result.success(salaryService.getSalaryTrend(months));
    }

    /**
     * 部门薪资分布
     */
    @GetMapping("/byDepartment")
    public Result<Map<String, Object>> byDepartment(@RequestParam String month) {
        return Result.success(salaryService.getSalaryByDepartment(month));
    }
}
