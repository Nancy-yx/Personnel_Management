package com.personnel.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 员工实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_employee")
public class Employee extends BaseEntity {

    /**
     * 工号
     */
    private String employeeNo;

    /**
     * 姓名
     */
    private String name;

    /**
     * 性别：1-男，2-女
     */
    private Integer gender;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 出生日期
     */
    private LocalDate birthDate;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 部门ID
     */
    private Long departmentId;

    /**
     * 职位
     */
    private String position;

    /**
     * 职级
     */
    private String level;

    /**
     * 入职日期
     */
    private LocalDate hireDate;

    /**
     * 转正日期
     */
    private LocalDate regularDate;

    /**
     * 状态：1-在职，2-离职，3-试用
     */
    private Integer status;

    /**
     * 离职日期
     */
    private LocalDate leaveDate;

    /**
     * 基本工资
     */
    private BigDecimal baseSalary;

    /**
     * 住址
     */
    private String address;

    /**
     * 紧急联系人
     */
    private String emergencyContact;

    /**
     * 紧急联系电话
     */
    private String emergencyPhone;

    /**
     * 备注
     */
    private String remark;

    /**
     * 部门名称（关联查询字段，非数据库字段）
     */
    @TableField(exist = false)
    private String departmentName;
}
