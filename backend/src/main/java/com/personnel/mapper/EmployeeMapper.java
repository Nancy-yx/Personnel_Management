package com.personnel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.personnel.dto.EmployeeQueryDTO;
import com.personnel.entity.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 员工Mapper
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {

    /**
     * 分页查询员工（带部门名）
     */
    IPage<Employee> selectEmployeePage(IPage<Employee> page, @Param("query") EmployeeQueryDTO queryDTO);

    /**
     * 按部门查询员工
     */
    List<Employee> selectByDepartmentId(@Param("departmentId") Long departmentId);
}
