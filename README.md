# 企业人事管理系统

基于 SpringBoot 3.x + Vue 3 + MySQL 8.x 的企业人事管理后台系统。

## 技术栈

### 后端
- Java 17
- Spring Boot 3.2.0
- Spring Security + JWT
- MyBatis-Plus 3.5.5
- MySQL 8.0+
- Hutool 5.8.25

### 前端
- Vue 3.4.0
- Vite 5.0.0
- Element Plus 2.5.0
- Pinia 2.1.7
- Vue Router 4.2.5
- ECharts 5.4.3
- Axios 1.6.5

## 功能模块

- **首页总览**：数据仪表盘，展示员工总数、在职/离职人数、部门分布、考勤异常、薪资总额等核心数据
- **员工管理**：员工信息增删改查、批量操作、多条件检索筛选
- **部门管理**：树形架构管理部门，支持新增、编辑、删除、调整部门层级
- **薪资管理**：员工基本工资、绩效、社保、扣款、实发工资管理，月度薪资核算
- **考勤管理**：打卡、迟到、早退、旷工、请假、加班统计，月度考勤汇总

## 项目结构

```
Personnel_Management/
├── backend/                    # 后端项目
│   ├── src/main/java/com/personnel/
│   │   ├── config/             # 配置类
│   │   ├── common/             # 通用类（Result、ErrorCode等）
│   │   ├── controller/         # 控制器层
│   │   ├── dto/                # 数据传输对象
│   │   ├── entity/             # 实体类
│   │   ├── mapper/             # 数据访问层
│   │   ├── service/            # 业务逻辑层
│   │   └── util/               # 工具类
│   └── src/main/resources/
│       ├── mapper/             # MyBatis XML映射文件
│       ├── application.yml     # 应用配置
│       └── schema.sql          # 数据库初始化脚本
├── frontend/                   # 前端项目
│   ├── src/
│   │   ├── api/                # API接口
│   │   ├── assets/styles/      # 样式文件
│   │   ├── layout/             # 布局组件
│   │   ├── router/             # 路由配置
│   │   ├── store/              # Pinia状态管理
│   │   ├── utils/              # 工具类
│   │   └── views/              # 页面组件
│   └── public/                 # 静态资源
└── README.md
```

## 快速开始

### 环境要求

- JDK 17+
- Node.js 18+
- MySQL 8.0+
- Maven 3.8+

### 1. 数据库准备

```bash
# 登录MySQL
mysql -u root -p

# 执行初始化脚本
source backend/src/main/resources/schema.sql
```

或手动执行 `schema.sql` 文件创建数据库和表。

### 2. 后端启动

```bash
cd backend

# 修改数据库连接配置（如需）
# 编辑 src/main/resources/application.yml
# spring.datasource.url
# spring.datasource.username
# spring.datasource.password

# Maven构建
mvn clean package -DskipTests

# 运行
java -jar target/personnel-management-1.0.0.jar
```

后端服务启动后访问：http://localhost:8080

### 3. 前端启动

```bash
cd frontend

# 安装依赖
npm install

# 开发模式运行
npm run dev

# 构建生产版本
npm run build
```

前端服务启动后访问：http://localhost:5173

## 默认账号

- 用户名：`admin`
- 密码：`admin123`

## API接口

### 认证接口
- `POST /api/auth/login` - 用户登录
- `GET /api/auth/userInfo` - 获取当前用户信息
- `PUT /api/auth/password` - 修改密码

### 仪表盘
- `GET /api/dashboard/overview` - 获取仪表盘数据
- `GET /api/dashboard/stats` - 获取统计数据

### 员工管理
- `GET /api/employees` - 分页查询员工列表
- `GET /api/employees/{id}` - 获取员工详情
- `POST /api/employees` - 新增员工
- `PUT /api/employees/{id}` - 修改员工
- `DELETE /api/employees/{id}` - 删除员工
- `DELETE /api/employees/batch` - 批量删除
- `GET /api/employees/generateNo` - 生成工号

### 部门管理
- `GET /api/departments/tree` - 获取部门树形结构
- `GET /api/departments/list` - 获取部门列表
- `GET /api/departments/all` - 获取所有部门
- `POST /api/departments` - 新增部门
- `PUT /api/departments/{id}` - 修改部门
- `DELETE /api/departments/{id}` - 删除部门
- `GET /api/departments/{id}/employees` - 获取部门员工

### 薪资管理
- `GET /api/salaries` - 分页查询薪资列表
- `GET /api/salaries/{id}` - 获取薪资详情
- `POST /api/salaries` - 新增薪资
- `PUT /api/salaries/{id}` - 修改薪资
- `DELETE /api/salaries/{id}` - 删除薪资
- `POST /api/salaries/calculate/{month}` - 核算月度薪资
- `GET /api/salaries/trend` - 薪资趋势
- `GET /api/salaries/byDepartment` - 部门薪资分布

### 考勤管理
- `GET /api/attendances` - 分页查询考勤列表
- `GET /api/attendances/{id}` - 获取考勤详情
- `POST /api/attendances` - 新增考勤
- `PUT /api/attendances/{id}` - 修改考勤
- `DELETE /api/attendances/{id}` - 删除考勤
- `POST /api/attendances/supplementary` - 手动补录
- `GET /api/attendances/stats` - 考勤统计
- `GET /api/attendances/abnormal` - 异常考勤
- `GET /api/attendances/summary/{month}` - 月度考勤汇总

## 部署说明

### 后端打包部署

```bash
cd backend
mvn clean package -DskipTests

# 运行
java -jar target/personnel-management-1.0.0.jar --spring.profiles.active=prod
```

### 前端打包部署

```bash
cd frontend
npm run build

# 将 dist 目录内容部署到 Web 服务器
# 可以使用 Nginx、Apache 等
```

### Nginx 配置示例

```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    # 前端
    location / {
        root /path/to/frontend/dist;
        index index.html;
        try_files $uri $uri/ /index.html;
    }
    
    # 后端API代理
    location /api/ {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

## 权限说明

- **ADMIN（管理员）**：拥有所有功能的操作权限，包括新增、编辑、删除
- **USER（普通用户）**：仅可查看数据，无法进行增删改操作

## 数据库表结构

| 表名 | 说明 |
|------|------|
| sys_user | 系统用户表 |
| sys_department | 部门表 |
| sys_employee | 员工表 |
| salary_record | 薪资记录表 |
| attendance_record | 考勤记录表 |

详细表结构请参考 `backend/src/main/resources/schema.sql`。

## 注意事项

1. 首次启动后端服务会自动创建默认管理员账号
2. 薪资核算功能会自动计算应发工资、社保、公积金、个税和实发工资
3. 考勤异常记录（迟到、早退、旷工）会在仪表盘和考勤列表中标红显示
4. 部门删除前需要先删除子部门和员工
