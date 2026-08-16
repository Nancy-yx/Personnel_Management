package com.personnel.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 部门实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_department")
public class Department extends BaseEntity {

    /**
     * 部门名称
     */
    private String name;

    /**
     * 父部门ID，0为顶级部门
     */
    private Long parentId;

    /**
     * 部门编码
     */
    private String code;

    /**
     * 部门负责人
     */
    private String leader;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}
