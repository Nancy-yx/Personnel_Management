package com.personnel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.personnel.common.PageResult;
import com.personnel.dto.SalaryQueryDTO;
import com.personnel.entity.Salary;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 薪资服务接口
 */
public interface SalaryService extends IService<Salary> {

    /**
     * 分页查询薪资
     */
    PageResult<Salary> getSalaryPage(SalaryQueryDTO queryDTO);

    /**
     * 根据ID获取薪资
     */
    Salary getSalaryById(Long id);

    /**
     * 新增薪资
     */
    Salary addSalary(Salary salary);

    /**
     * 修改薪资
     */
    Salary updateSalary(Salary salary);

    /**
     * 删除薪资
     */
    void deleteSalary(Long id);

    /**
     * 核算月度薪资
     */
    void calculateMonthlySalary(String month);

    /**
     * 获取薪资趋势
     */
    Map<String, Object> getSalaryTrend(Integer months);

    /**
     * 按部门获取薪资分布
     */
    Map<String, Object> getSalaryByDepartment(String month);

    /**
     * 获取指定月份薪资总额
     */
    BigDecimal getTotalSalaryByMonth(String month);
}
