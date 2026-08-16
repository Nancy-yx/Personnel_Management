package com.personnel.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.personnel.common.BusinessException;
import com.personnel.common.ErrorCode;
import com.personnel.common.PageResult;
import com.personnel.dto.SalaryQueryDTO;
import com.personnel.entity.Employee;
import com.personnel.entity.Salary;
import com.personnel.mapper.SalaryMapper;
import com.personnel.service.EmployeeService;
import com.personnel.service.SalaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 薪资服务实现
 */
@Service
@RequiredArgsConstructor
public class SalaryServiceImpl extends ServiceImpl<SalaryMapper, Salary> implements SalaryService {

    private final EmployeeService employeeService;

    @Override
    public PageResult<Salary> getSalaryPage(SalaryQueryDTO queryDTO) {
        Page<Salary> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        IPage<Salary> result = baseMapper.selectSalaryPage(page, queryDTO);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords());
    }

    @Override
    public Salary getSalaryById(Long id) {
        Salary salary = this.getById(id);
        if (salary == null) {
            throw new BusinessException(ErrorCode.SALARY_NOT_EXIST);
        }
        return salary;
    }

    @Override
    public Salary addSalary(Salary salary) {
        // 自动计算应发工资和实发工资
        calculateSalaryFields(salary);
        this.save(salary);
        return salary;
    }

    @Override
    public Salary updateSalary(Salary salary) {
        if (this.getById(salary.getId()) == null) {
            throw new BusinessException(ErrorCode.SALARY_NOT_EXIST);
        }
        // 自动计算应发工资和实发工资
        calculateSalaryFields(salary);
        this.updateById(salary);
        return salary;
    }

    @Override
    public void deleteSalary(Long id) {
        if (this.getById(id) == null) {
            throw new BusinessException(ErrorCode.SALARY_NOT_EXIST);
        }
        this.removeById(id);
    }

    /**
     * 计算薪资字段：应发工资 = 各项补贴之和；实发工资 = 应发 - 各项扣款之和
     */
    private void calculateSalaryFields(Salary salary) {
        BigDecimal base = nz(salary.getBaseSalary());
        BigDecimal performance = nz(salary.getPerformanceBonus());
        BigDecimal position = nz(salary.getPositionAllowance());
        BigDecimal transport = nz(salary.getTransportAllowance());
        BigDecimal communication = nz(salary.getCommunicationAllowance());
        BigDecimal otherAllowance = nz(salary.getOtherAllowance());
        BigDecimal gross = base.add(performance).add(position).add(transport).add(communication).add(otherAllowance);
        salary.setGrossSalary(gross);

        BigDecimal social = nz(salary.getSocialInsurance());
        BigDecimal housing = nz(salary.getHousingFund());
        BigDecimal tax = nz(salary.getPersonalTax());
        BigDecimal otherDeduction = nz(salary.getOtherDeduction());
        BigDecimal net = gross.subtract(social).subtract(housing).subtract(tax).subtract(otherDeduction);
        salary.setNetSalary(net);
    }

    /**
     * BigDecimal 空值转零
     */
    private BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void calculateMonthlySalary(String month) {
        // 查询当月所有在职员工
        List<Employee> employees = employeeService.lambdaQuery()
                .eq(Employee::getStatus, 1)
                .list();

        for (Employee employee : employees) {
            // 检查该员工当月是否已有薪资记录
            Salary existing = this.lambdaQuery()
                    .eq(Salary::getEmployeeId, employee.getId())
                    .eq(Salary::getMonth, month)
                    .one();
            if (existing != null) {
                // 已存在则跳过
                continue;
            }
            Salary salary = new Salary();
            salary.setEmployeeId(employee.getId());
            salary.setMonth(month);
            salary.setBaseSalary(employee.getBaseSalary() == null ? BigDecimal.ZERO : employee.getBaseSalary());
            salary.setPerformanceBonus(BigDecimal.ZERO);
            salary.setPositionAllowance(BigDecimal.ZERO);
            salary.setTransportAllowance(BigDecimal.ZERO);
            salary.setCommunicationAllowance(BigDecimal.ZERO);
            salary.setOtherAllowance(BigDecimal.ZERO);
            salary.setSocialInsurance(BigDecimal.ZERO);
            salary.setHousingFund(BigDecimal.ZERO);
            salary.setPersonalTax(BigDecimal.ZERO);
            salary.setOtherDeduction(BigDecimal.ZERO);
            salary.setStatus(0);
            calculateSalaryFields(salary);
            this.save(salary);
        }
    }

    @Override
    public Map<String, Object> getSalaryTrend(Integer months) {
        Map<String, Object> data = new HashMap<>();
        if (months == null || months <= 0) {
            months = 6;
        }
        List<Map<String, Object>> trendList = baseMapper.selectSalaryTrend(months);
        // 构造月份和总额列表
        List<String> monthList = new java.util.ArrayList<>();
        List<BigDecimal> totalList = new java.util.ArrayList<>();

        // 生成最近几个月的月份字符串
        LocalDate now = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM");
        java.util.Map<String, BigDecimal> monthMap = new java.util.HashMap<>();
        for (Map<String, Object> row : trendList) {
            String m = (String) row.get("month");
            Object totalObj = row.get("totalSalary");
            BigDecimal total = toBigDecimal(totalObj);
            monthMap.put(m, total);
        }
        for (int i = months - 1; i >= 0; i--) {
            String m = now.minusMonths(i).format(fmt);
            monthList.add(m);
            totalList.add(monthMap.getOrDefault(m, BigDecimal.ZERO));
        }
        data.put("months", monthList);
        data.put("totals", totalList);
        return data;
    }

    @Override
    public Map<String, Object> getSalaryByDepartment(String month) {
        Map<String, Object> data = new HashMap<>();
        List<Map<String, Object>> list = baseMapper.selectSalaryByDepartment(month);
        List<String> departments = new java.util.ArrayList<>();
        List<BigDecimal> totals = new java.util.ArrayList<>();
        for (Map<String, Object> row : list) {
            departments.add((String) row.get("departmentName"));
            totals.add(toBigDecimal(row.get("totalSalary")));
        }
        data.put("departments", departments);
        data.put("totals", totals);
        return data;
    }

    @Override
    public BigDecimal getTotalSalaryByMonth(String month) {
        Map<String, Object> result = baseMapper.selectTotalByMonth(month);
        if (result == null) {
            return BigDecimal.ZERO;
        }
        return toBigDecimal(result.get("totalSalary"));
    }

    /**
     * 将对象转为 BigDecimal
     */
    private BigDecimal toBigDecimal(Object obj) {
        if (obj == null) {
            return BigDecimal.ZERO;
        }
        if (obj instanceof BigDecimal) {
            return (BigDecimal) obj;
        }
        if (obj instanceof Number) {
            return new BigDecimal(((Number) obj).toString());
        }
        try {
            return new BigDecimal(obj.toString());
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
}
