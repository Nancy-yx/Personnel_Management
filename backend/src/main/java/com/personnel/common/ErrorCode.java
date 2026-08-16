package com.personnel.common;

/**
 * 错误码枚举
 */
public enum ErrorCode {

    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权，请先登录"),
    FORBIDDEN(403, "权限不足"),
    NOT_FOUND(404, "资源不存在"),
    INTERNAL_ERROR(500, "服务器内部错误"),

    // 业务错误码 1000-1999
    USER_NOT_EXIST(1001, "用户不存在"),
    USER_PASSWORD_ERROR(1002, "密码错误"),
    USER_DISABLED(1003, "用户已被禁用"),
    USERNAME_EXIST(1004, "用户名已存在"),

    // 员工相关 2000-2999
    EMPLOYEE_NOT_EXIST(2001, "员工不存在"),
    EMPLOYEE_NO_EXIST(2002, "工号已存在"),

    // 部门相关 3000-3999
    DEPARTMENT_NOT_EXIST(3001, "部门不存在"),
    DEPARTMENT_HAS_CHILDREN(3002, "部门下存在子部门，无法删除"),
    DEPARTMENT_HAS_EMPLOYEES(3003, "部门下存在员工，无法删除"),

    // 薪资相关 4000-4999
    SALARY_NOT_EXIST(4001, "薪资记录不存在"),

    // 考勤相关 5000-5999
    ATTENDANCE_NOT_EXIST(5001, "考勤记录不存在");

    private final Integer code;
    private final String message;

    /**
     * 构造函数
     * @param code 错误码
     * @param message 错误信息
     */
    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 获取错误码
     * @return Integer
     */
    public Integer getCode() {
        return code;
    }

    /**
     * 获取错误信息
     * @return String
     */
    public String getMessage() {
        return message;
    }
}