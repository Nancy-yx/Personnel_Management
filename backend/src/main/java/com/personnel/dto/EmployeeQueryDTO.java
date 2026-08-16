package com.personnel.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 员工查询DTO
 */
@Data
public class EmployeeQueryDTO implements Serializable {

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页条数
     */
    private Integer pageSize = 10;

    /**
     * 员工姓名
     */
    private String name;

    /**
     * 部门ID
     */
    private Long departmentId;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 职位
     */
    private String position;
}
