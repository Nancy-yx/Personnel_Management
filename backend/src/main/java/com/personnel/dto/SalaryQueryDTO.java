package com.personnel.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 薪资查询DTO
 */
@Data
public class SalaryQueryDTO implements Serializable {

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页条数
     */
    private Integer pageSize = 10;

    /**
     * 员工ID
     */
    private Long employeeId;

    /**
     * 薪资月份
     */
    private String month;

    /**
     * 状态
     */
    private Integer status;
}
