package com.personnel.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 考勤记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("attendance_record")
public class Attendance extends BaseEntity {

    /**
     * 员工ID
     */
    private Long employeeId;

    /**
     * 考勤日期
     */
    private LocalDate attendanceDate;

    /**
     * 上班打卡时间
     */
    private LocalTime checkInTime;

    /**
     * 下班打卡时间
     */
    private LocalTime checkOutTime;

    /**
     * 出勤状态：normal-正常，late-迟到，early-早退，absent-旷工，leave-请假，overtime-加班
     */
    private String status;

    /**
     * 迟到/早退时长（分钟）
     */
    private Integer duration;

    /**
     * 是否补卡：0-否，1-是
     */
    private Integer isSupplementary;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 员工姓名（关联查询字段，非数据库字段）
     */
    @TableField(exist = false)
    private String employeeName;
}
