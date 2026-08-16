package com.personnel.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 薪资记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("salary_record")
public class Salary extends BaseEntity {

    /**
     * 员工ID
     */
    private Long employeeId;

    /**
     * 薪资月份（格式：2024-01）
     */
    private String month;

    /**
     * 基本工资
     */
    private BigDecimal baseSalary;

    /**
     * 绩效奖金
     */
    private BigDecimal performanceBonus;

    /**
     * 岗位津贴
     */
    private BigDecimal positionAllowance;

    /**
     * 交通补贴
     */
    private BigDecimal transportAllowance;

    /**
     * 通讯补贴
     */
    private BigDecimal communicationAllowance;

    /**
     * 其他补贴
     */
    private BigDecimal otherAllowance;

    /**
     * 应发工资
     */
    private BigDecimal grossSalary;

    /**
     * 社保扣除
     */
    private BigDecimal socialInsurance;

    /**
     * 公积金扣除
     */
    private BigDecimal housingFund;

    /**
     * 个人所得税
     */
    private BigDecimal personalTax;

    /**
     * 其他扣款
     */
    private BigDecimal otherDeduction;

    /**
     * 实发工资
     */
    private BigDecimal netSalary;

    /**
     * 状态：0-待核算，1-已核算，2-已发放
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 员工姓名（关联查询字段，非数据库字段）
     */
    @TableField(exist = false)
    private String employeeName;

    /**
     * 员工工号（关联查询字段，非数据库字段）
     */
    @TableField(exist = false)
    private String employeeNo;
}
