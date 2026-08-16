package com.personnel.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.personnel.common.BusinessException;
import com.personnel.common.ErrorCode;
import com.personnel.common.PageResult;
import com.personnel.dto.EmployeeQueryDTO;
import com.personnel.entity.Department;
import com.personnel.entity.Employee;
import com.personnel.mapper.EmployeeMapper;
import com.personnel.service.DepartmentService;
import com.personnel.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 员工服务实现
 */
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

    private final DepartmentService departmentService;

    @Override
    public PageResult<Employee> getEmployeePage(EmployeeQueryDTO queryDTO) {
        Page<Employee> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        IPage<Employee> result = baseMapper.selectEmployeePage(page, queryDTO);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords());
    }

    @Override
    public Employee getEmployeeById(Long id) {
        Employee employee = this.getById(id);
        if (employee == null) {
            throw new BusinessException(ErrorCode.EMPLOYEE_NOT_EXIST);
        }
        return employee;
    }

    @Override
    public Employee addEmployee(Employee employee) {
        // 如果未提供工号，则自动生成
        if (!StringUtils.hasText(employee.getEmployeeNo())) {
            employee.setEmployeeNo(generateEmployeeNo());
        } else {
            // 检查工号是否已存在
            long count = this.lambdaQuery().eq(Employee::getEmployeeNo, employee.getEmployeeNo()).count();
            if (count > 0) {
                throw new BusinessException(ErrorCode.EMPLOYEE_NO_EXIST);
            }
        }
        this.save(employee);
        return employee;
    }

    @Override
    public Employee updateEmployee(Employee employee) {
        if (this.getById(employee.getId()) == null) {
            throw new BusinessException(ErrorCode.EMPLOYEE_NOT_EXIST);
        }
        this.updateById(employee);
        return employee;
    }

    @Override
    public void deleteEmployee(Long id) {
        if (this.getById(id) == null) {
            throw new BusinessException(ErrorCode.EMPLOYEE_NOT_EXIST);
        }
        this.removeById(id);
    }

    @Override
    public void batchDelete(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        this.removeBatchByIds(ids);
    }

    @Override
    public String generateEmployeeNo() {
        // 工号格式：EMP + yyyyMMdd + 4位序号
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "EMP" + dateStr;
        // 查询当天最大序号
        Employee lastEmployee = this.lambdaQuery()
                .likeRight(Employee::getEmployeeNo, prefix)
                .orderByDesc(Employee::getEmployeeNo)
                .last("LIMIT 1")
                .one();
        int seq = 1;
        if (lastEmployee != null && lastEmployee.getEmployeeNo() != null) {
            String lastNo = lastEmployee.getEmployeeNo();
            String seqStr = lastNo.substring(prefix.length());
            try {
                seq = Integer.parseInt(seqStr) + 1;
            } catch (NumberFormatException ignored) {
            }
        }
        return prefix + String.format("%04d", seq);
    }

    @Override
    public Map<String, Object> getDashboardData() {
        Map<String, Object> data = new HashMap<>();
        // 员工总数
        long totalEmployees = this.count();
        data.put("totalEmployees", totalEmployees);
        // 在职人数
        long activeEmployees = this.lambdaQuery().eq(Employee::getStatus, 1).count();
        data.put("activeEmployees", activeEmployees);
        // 试用期人数
        long onProbation = this.lambdaQuery().eq(Employee::getStatus, 3).count();
        data.put("onProbation", onProbation);
        // 离职人数
        long resigned = this.lambdaQuery().eq(Employee::getStatus, 2).count();
        data.put("resigned", resigned);

        // 部门分布
        List<Map<String, Object>> departmentDistribution = new ArrayList<>();
        List<Department> departments = departmentService.getAll();
        for (Department dept : departments) {
            long count = this.lambdaQuery().eq(Employee::getDepartmentId, dept.getId())
                    .eq(Employee::getStatus, 1).count();
            if (count > 0) {
                Map<String, Object> item = new HashMap<>();
                item.put("name", dept.getName());
                item.put("value", count);
                departmentDistribution.add(item);
            }
        }
        data.put("departmentDistribution", departmentDistribution);
        return data;
    }
}
