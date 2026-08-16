package com.personnel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.personnel.entity.Department;
import org.apache.ibatis.annotations.Mapper;

/**
 * 部门Mapper
 */
@Mapper
public interface DepartmentMapper extends BaseMapper<Department> {
}
