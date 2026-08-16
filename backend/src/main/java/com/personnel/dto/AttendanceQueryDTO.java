package com.personnel.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 考勤查询DTO
 */
@Data
public class AttendanceQueryDTO implements Serializable {

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
     * 开始日期
     */
    private String startDate;

    /**
     * 结束日期
     */
    private String endDate;

    /**
     * 出勤状态
     */
    private String status;
}
