package com.personnel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.personnel.dto.SalaryQueryDTO;
import com.personnel.entity.Salary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 薪资Mapper
 */
@Mapper
public interface SalaryMapper extends BaseMapper<Salary> {

    /**
     * 分页查询薪资（带员工姓名工号）
     */
    IPage<Salary> selectSalaryPage(IPage<Salary> page, @Param("query") SalaryQueryDTO queryDTO);

    /**
     * 按月份汇总薪资总额
     */
    Map<String, Object> selectTotalByMonth(@Param("month") String month);

    /**
     * 查询最近几个月的薪资汇总
     */
    List<Map<String, Object>> selectSalaryTrend(@Param("months") Integer months);

    /**
     * 按部门统计薪资
     */
    List<Map<String, Object>> selectSalaryByDepartment(@Param("month") String month);
}
