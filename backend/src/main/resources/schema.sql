-- =============================================
-- 企业人事管理系统 数据库初始化脚本
-- 数据库：MySQL 8.0+
-- =============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS personnel_management 
DEFAULT CHARACTER SET utf8mb4 
DEFAULT COLLATE utf8mb4_unicode_ci;

USE personnel_management;

-- =============================================
-- 1. 系统用户表
-- =============================================
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码（加密存储）',
    real_name VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
    role VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色：ADMIN-管理员，USER-普通用户',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    employee_id BIGINT DEFAULT NULL COMMENT '关联员工ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- =============================================
-- 2. 部门表
-- =============================================
DROP TABLE IF EXISTS sys_department;
CREATE TABLE sys_department (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    name VARCHAR(100) NOT NULL COMMENT '部门名称',
    parent_id BIGINT NOT NULL DEFAULT 0 COMMENT '父部门ID，0为顶级部门',
    code VARCHAR(50) DEFAULT NULL COMMENT '部门编码',
    leader VARCHAR(50) DEFAULT NULL COMMENT '部门负责人',
    phone VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    email VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    sort INT NOT NULL DEFAULT 0 COMMENT '排序',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (id),
    KEY idx_parent_id (parent_id),
    KEY idx_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- =============================================
-- 3. 员工表
-- =============================================
DROP TABLE IF EXISTS sys_employee;
CREATE TABLE sys_employee (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    employee_no VARCHAR(50) NOT NULL COMMENT '工号',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    gender TINYINT NOT NULL DEFAULT 1 COMMENT '性别：1-男，2-女',
    id_card VARCHAR(20) DEFAULT NULL COMMENT '身份证号',
    birth_date DATE DEFAULT NULL COMMENT '出生日期',
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    email VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    department_id BIGINT DEFAULT NULL COMMENT '部门ID',
    position VARCHAR(50) DEFAULT NULL COMMENT '职位',
    level VARCHAR(20) DEFAULT NULL COMMENT '职级',
    hire_date DATE DEFAULT NULL COMMENT '入职日期',
    regular_date DATE DEFAULT NULL COMMENT '转正日期',
    status TINYINT NOT NULL DEFAULT 3 COMMENT '状态：1-在职，2-离职，3-试用',
    leave_date DATE DEFAULT NULL COMMENT '离职日期',
    base_salary DECIMAL(10, 2) DEFAULT NULL COMMENT '基本工资',
    address VARCHAR(255) DEFAULT NULL COMMENT '住址',
    emergency_contact VARCHAR(50) DEFAULT NULL COMMENT '紧急联系人',
    emergency_phone VARCHAR(20) DEFAULT NULL COMMENT '紧急联系电话',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_employee_no (employee_no),
    KEY idx_department_id (department_id),
    KEY idx_status (status),
    KEY idx_hire_date (hire_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工表';

-- =============================================
-- 4. 薪资记录表
-- =============================================
DROP TABLE IF EXISTS salary_record;
CREATE TABLE salary_record (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    employee_id BIGINT NOT NULL COMMENT '员工ID',
    month VARCHAR(7) NOT NULL COMMENT '薪资月份（格式：2024-01）',
    base_salary DECIMAL(10, 2) NOT NULL DEFAULT 0 COMMENT '基本工资',
    performance_bonus DECIMAL(10, 2) NOT NULL DEFAULT 0 COMMENT '绩效奖金',
    position_allowance DECIMAL(10, 2) NOT NULL DEFAULT 0 COMMENT '岗位津贴',
    transport_allowance DECIMAL(10, 2) NOT NULL DEFAULT 0 COMMENT '交通补贴',
    communication_allowance DECIMAL(10, 2) NOT NULL DEFAULT 0 COMMENT '通讯补贴',
    other_allowance DECIMAL(10, 2) NOT NULL DEFAULT 0 COMMENT '其他补贴',
    gross_salary DECIMAL(10, 2) NOT NULL DEFAULT 0 COMMENT '应发工资',
    social_insurance DECIMAL(10, 2) NOT NULL DEFAULT 0 COMMENT '社保扣除',
    housing_fund DECIMAL(10, 2) NOT NULL DEFAULT 0 COMMENT '公积金扣除',
    personal_tax DECIMAL(10, 2) NOT NULL DEFAULT 0 COMMENT '个人所得税',
    other_deduction DECIMAL(10, 2) NOT NULL DEFAULT 0 COMMENT '其他扣款',
    net_salary DECIMAL(10, 2) NOT NULL DEFAULT 0 COMMENT '实发工资',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-待核算，1-已核算，2-已发放',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (id),
    KEY idx_employee_id (employee_id),
    KEY idx_month (month),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='薪资记录表';

-- =============================================
-- 5. 考勤记录表
-- =============================================
DROP TABLE IF EXISTS attendance_record;
CREATE TABLE attendance_record (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    employee_id BIGINT NOT NULL COMMENT '员工ID',
    attendance_date DATE NOT NULL COMMENT '考勤日期',
    check_in_time TIME DEFAULT NULL COMMENT '上班打卡时间',
    check_out_time TIME DEFAULT NULL COMMENT '下班打卡时间',
    status VARCHAR(20) NOT NULL DEFAULT 'normal' COMMENT '出勤状态：normal-正常，late-迟到，early-早退，absent-旷工，leave-请假，overtime-加班',
    duration INT DEFAULT NULL COMMENT '迟到/早退时长（分钟）',
    is_supplementary TINYINT NOT NULL DEFAULT 0 COMMENT '是否补卡：0-否，1-是',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_by VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (id),
    KEY idx_employee_id (employee_id),
    KEY idx_attendance_date (attendance_date),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考勤记录表';

-- =============================================
-- 初始数据
-- =============================================

-- 初始化系统用户（密码：admin123，使用BCrypt加密）
INSERT INTO sys_user (username, password, real_name, role, status) VALUES
('admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '系统管理员', 'ADMIN', 1);

-- 初始化部门数据
INSERT INTO sys_department (name, parent_id, code, leader, phone, email, sort, status) VALUES
('总经办', 0, 'CEO', '张三', '13800000001', 'ceo@company.com', 1, 1),
('技术部', 0, 'TECH', '李四', '13800000002', 'tech@company.com', 2, 1),
('人力资源部', 0, 'HR', '王五', '13800000003', 'hr@company.com', 3, 1),
('财务部', 0, 'FINANCE', '赵六', '13800000004', 'finance@company.com', 4, 1),
('市场部', 0, 'MARKET', '钱七', '13800000005', 'market@company.com', 5, 1);

-- 技术部子部门
INSERT INTO sys_department (name, parent_id, code, leader, phone, email, sort, status) VALUES
('前端开发组', 2, 'TECH-FE', '孙八', '13800000006', 'fe@company.com', 1, 1),
('后端开发组', 2, 'TECH-BE', '周九', '13800000007', 'be@company.com', 2, 1),
('测试组', 2, 'TECH-QA', '吴十', '13800000008', 'qa@company.com', 3, 1);

-- 初始化员工数据
INSERT INTO sys_employee (employee_no, name, gender, id_card, birth_date, phone, email, department_id, position, level, hire_date, status, base_salary) VALUES
('EMP2024010001', '张三', 1, '110101199001011234', '1990-01-01', '13800000001', 'zhangsan@company.com', 1, '总经理', 'M5', '2020-01-01', 1, 25000.00),
('EMP2024010002', '李四', 1, '110101199203021234', '1992-03-02', '13800000002', 'lisi@company.com', 2, '技术总监', 'M4', '2021-03-15', 1, 20000.00),
('EMP2024010003', '王五', 2, '110101199505031234', '1995-05-03', '13800000003', 'wangwu@company.com', 3, 'HR经理', 'M3', '2022-05-01', 1, 15000.00),
('EMP2024010004', '赵六', 1, '110101199307041234', '1993-07-04', '13800000004', 'zhaoliu@company.com', 4, '财务经理', 'M3', '2021-07-01', 1, 15000.00),
('EMP2024010005', '钱七', 2, '110101199608051234', '1996-08-05', '13800000005', 'qianqi@company.com', 5, '市场经理', 'M3', '2022-08-15', 1, 15000.00),
('EMP2024010006', '孙八', 1, '110101199709061234', '1997-09-06', '13800000006', 'sunba@company.com', 6, '前端工程师', 'P5', '2023-09-01', 1, 12000.00),
('EMP2024010007', '周九', 1, '110101199710071234', '1997-10-07', '13800000007', 'zhoujiu@company.com', 7, '后端工程师', 'P5', '2023-10-01', 1, 13000.00),
('EMP2024010008', '吴十', 2, '110101199811081234', '1998-11-08', '13800000008', 'wushi@company.com', 8, '测试工程师', 'P4', '2023-11-01', 3, 10000.00),
('EMP2024010009', '郑十一', 1, '110101199512091234', '1995-12-09', '13800000009', 'zheng11@company.com', 6, '高级前端', 'P6', '2022-12-01', 1, 18000.00),
('EMP2024010010', '冯十二', 1, '110101199601101234', '1996-01-10', '13800000010', 'feng12@company.com', 7, '高级后端', 'P6', '2022-01-15', 1, 20000.00);

-- 初始化考勤数据（最近一个月）
INSERT INTO attendance_record (employee_id, attendance_date, check_in_time, check_out_time, status, create_by) VALUES
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), '08:55:00', '18:05:00', 'normal', 'admin'),
(2, DATE_SUB(CURDATE(), INTERVAL 1 DAY), '09:15:00', '18:10:00', 'late', 'admin'),
(3, DATE_SUB(CURDATE(), INTERVAL 1 DAY), '08:50:00', '17:45:00', 'early', 'admin'),
(4, DATE_SUB(CURDATE(), INTERVAL 1 DAY), '08:58:00', '18:02:00', 'normal', 'admin'),
(5, DATE_SUB(CURDATE(), INTERVAL 1 DAY), NULL, NULL, 'absent', 'admin'),
(6, DATE_SUB(CURDATE(), INTERVAL 1 DAY), '08:50:00', '19:30:00', 'overtime', 'admin'),
(7, DATE_SUB(CURDATE(), INTERVAL 1 DAY), '09:05:00', '18:00:00', 'normal', 'admin'),
(8, DATE_SUB(CURDATE(), INTERVAL 1 DAY), '08:55:00', '18:05:00', 'normal', 'admin'),
(9, DATE_SUB(CURDATE(), INTERVAL 1 DAY), '09:10:00', '18:00:00', 'late', 'admin'),
(10, DATE_SUB(CURDATE(), INTERVAL 1 DAY), '08:45:00', '20:00:00', 'overtime', 'admin'),
(1, DATE_SUB(CURDATE(), INTERVAL 2 DAY), '08:55:00', '18:00:00', 'normal', 'admin'),
(2, DATE_SUB(CURDATE(), INTERVAL 2 DAY), '08:58:00', '18:05:00', 'normal', 'admin'),
(3, DATE_SUB(CURDATE(), INTERVAL 2 DAY), '09:20:00', '18:00:00', 'late', 'admin'),
(6, DATE_SUB(CURDATE(), INTERVAL 2 DAY), '08:52:00', '18:10:00', 'normal', 'admin'),
(7, DATE_SUB(CURDATE(), INTERVAL 2 DAY), '08:50:00', '18:05:00', 'normal', 'admin');

-- 初始化薪资数据
INSERT INTO salary_record (employee_id, month, base_salary, performance_bonus, position_allowance, transport_allowance, communication_allowance, other_allowance, social_insurance, housing_fund, personal_tax, other_deduction) VALUES
(1, DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 1 MONTH), '%Y-%m'), 25000.00, 5000.00, 3000.00, 500.00, 300.00, 1000.00, 3480.00, 4176.00, 3272.80, 0),
(2, DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 1 MONTH), '%Y-%m'), 20000.00, 4000.00, 2500.00, 500.00, 300.00, 800.00, 2730.00, 3276.00, 2226.80, 0),
(3, DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 1 MONTH), '%Y-%m'), 15000.00, 3000.00, 2000.00, 500.00, 300.00, 500.00, 2130.00, 2556.00, 1174.80, 0),
(4, DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 1 MONTH), '%Y-%m'), 15000.00, 3000.00, 2000.00, 500.00, 300.00, 500.00, 2130.00, 2556.00, 1174.80, 0),
(5, DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 1 MONTH), '%Y-%m'), 15000.00, 3000.00, 2000.00, 500.00, 300.00, 500.00, 2130.00, 2556.00, 1174.80, 0),
(6, DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 1 MONTH), '%Y-%m'), 12000.00, 2000.00, 1500.00, 500.00, 200.00, 300.00, 1650.00, 1980.00, 528.00, 0),
(7, DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 1 MONTH), '%Y-%m'), 13000.00, 2500.00, 1500.00, 500.00, 200.00, 400.00, 1815.00, 2178.00, 797.20, 0),
(8, DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 1 MONTH), '%Y-%m'), 10000.00, 1500.00, 1000.00, 500.00, 200.00, 200.00, 1365.00, 1638.00, 145.20, 0),
(9, DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 1 MONTH), '%Y-%m'), 18000.00, 4000.00, 2000.00, 500.00, 300.00, 600.00, 2478.00, 2973.60, 1852.40, 0),
(10, DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 1 MONTH), '%Y-%m'), 20000.00, 4500.00, 2500.00, 500.00, 300.00, 700.00, 2805.00, 3366.00, 2479.20, 0);

-- 更新薪资的应发工资和实发工资
UPDATE salary_record SET 
    gross_salary = base_salary + performance_bonus + position_allowance + transport_allowance + communication_allowance + other_allowance,
    net_salary = base_salary + performance_bonus + position_allowance + transport_allowance + communication_allowance + other_allowance 
                 - social_insurance - housing_fund - personal_tax - other_deduction;
