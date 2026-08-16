# 企业人事管理系统 - 部署文档

> 本文档详细说明企业人事管理系统的项目概述、技术栈选型、完整部署流程及常见问题排查。

---

## 一、项目概述

### 1.1 项目简介

企业人事管理系统是一套面向中大型企业的 B/S 架构后台管理系统，基于 **前后端分离** 模式开发，覆盖企业人力资源管理的核心业务场景。系统提供从员工入职、部门组织、考勤记录到薪资核算的全流程数字化管理能力，并配套数据仪表盘用于经营决策支撑。

### 1.2 功能模块

| 模块 | 核心能力 |
|------|---------|
| 首页总览 | 数据仪表盘：员工总数、在职/离职人数、部门分布、考勤异常、薪资总额等核心指标可视化 |
| 员工管理 | 员工档案增删改查、批量操作、多条件检索（姓名/工号/部门/状态）、自动工号生成 |
| 部门管理 | 树形架构管理部门，支持新增、编辑、删除、层级调整、部门负责人维护 |
| 薪资管理 | 基本工资、绩效、社保、公积金、个税、扣款、实发工资的月度核算与发放管理 |
| 考勤管理 | 打卡记录、迟到/早退/旷工/请假/加班统计、手动补录、月度考勤汇总、异常预警 |
| 系统权限 | 基于 JWT 的认证机制，ADMIN（管理员）/ USER（普通用户）两级角色权限控制 |

### 1.3 业务架构

```
┌─────────────────────────────────────────────────┐
│                   前端（Vue3 + Element Plus）            │
│   浏览器 → Vite Dev Server / Nginx 静态托管              │
└───────────────────────┬─────────────────────────┘
                        │ HTTP / JWT
┌───────────────────────▼─────────────────────────┐
│              后端（Spring Boot 3 + Spring Security）       │
│   Controller → Service → Mapper → MySQL                  │
└───────────────────────┬─────────────────────────┘
                        │ JDBC
                ┌───────▼───────┐
                │   MySQL 8.0   │
                │  utf8mb4 编码  │
                └───────────────┘
```

### 1.4 数据库表结构

| 表名 | 说明 | 关键字段 |
|------|------|---------|
| `sys_user` | 系统用户表 | username, password(BCrypt), role(ADMIN/USER), employee_id |
| `sys_department` | 部门表 | name, parent_id(树形), code, leader, sort |
| `sys_employee` | 员工表 | name, employee_no, department_id, status, entry_date |
| `salary_record` | 薪资记录表 | employee_id, month, base_salary, bonus, deduction, net_salary |
| `attendance_record` | 考勤记录表 | employee_id, attendance_date, check_in_time, status, duration |

所有业务表均采用 `deleted` 字段做逻辑删除（0-未删除，1-已删除）。

---

## 二、技术栈

### 2.1 后端技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| JDK | 17 | Java 运行环境（LTS 版本） |
| Spring Boot | 3.2.0 | 主框架，简化 Spring 应用开发 |
| Spring Security | 6.x（随 Boot） | 认证授权框架 |
| MyBatis-Plus | 3.5.5 | MyBatis 增强工具，CRUD 简化、分页、逻辑删除 |
| MySQL Connector/J | 8.x | MySQL JDBC 驱动 |
| JJWT | 0.11.5 | JWT Token 生成与解析 |
| Hutool | 5.8.25 | Java 工具类库（加密、日期、Excel 等） |
| Apache POI | 5.2.5 | Excel 导入导出 |
| Lombok | 1.18.36 | 简化 POJO 样板代码 |
| Spring Validation | 随 Boot | 参数校验（@Valid / @NotBlank 等） |
| Jackson JSR310 | 随 Boot | Java 8 日期时间序列化 |
| Maven | 3.8+ | 项目构建与依赖管理 |

### 2.2 前端技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue.js | 3.4.0 | 渐进式前端框架（Composition API） |
| Vite | 5.0.0 | 构建工具与开发服务器 |
| Element Plus | 2.5.0 | Vue3 UI 组件库 |
| @element-plus/icons-vue | 2.3.1 | Element Plus 图标库 |
| Vue Router | 4.2.5 | 前端路由 |
| Pinia | 2.1.7 | 状态管理（替代 Vuex） |
| Axios | 1.6.5 | HTTP 请求库 |
| ECharts | 5.4.3 | 数据可视化图表 |
| vue-echarts | 6.6.1 | ECharts 的 Vue 封装 |
| Day.js | 1.11.10 | 轻量日期处理 |
| Sass | 1.69.7 | CSS 预处理器 |
| unplugin-auto-import | 0.17.5 | API 自动导入 |
| unplugin-vue-components | 0.26.0 | 组件自动导入 |

### 2.3 中间件与运行环境

| 组件 | 版本要求 | 说明 |
|------|---------|------|
| MySQL | 8.0+ | 数据存储，utf8mb4 编码 |
| Node.js | 18+ | 前端构建运行环境 |
| JDK | 17+ | 后端运行环境 |
| Maven | 3.8+ | 后端构建 |
| Nginx | 1.18+（可选） | 生产环境前端托管与 API 反向代理 |

---

## 三、环境准备

### 3.1 软件安装清单

部署前请确认以下软件已安装，并配置好环境变量（命令行可调用）：

1. **JDK 17+**：`java -version` 验证
2. **Maven 3.8+**：`mvn -version` 验证
3. **Node.js 18+**：`node -v` 验证（自带 npm）
4. **MySQL 8.0+**：`mysql --version` 验证
5. **Nginx**（生产环境可选）：`nginx -v` 验证

### 3.2 获取项目源码

```bash
# 进入工作目录
cd /path/to/workspace

# 项目源码已存在于本地：Personnel_Management/
# 目录结构：
# Personnel_Management/
# ├── backend/      # 后端 Spring Boot 工程
# ├── frontend/     # 前端 Vue3 工程
# ├── README.md     # 项目说明
# └── DEPLOY.md     # 本部署文档
```

---

## 四、部署流程（攻克流程）

本章按 **数据库 → 后端 → 前端 → Nginx** 的顺序，给出从零到可访问的完整部署步骤。每步均附验证方法，便于定位问题。

### 4.1 第一步：数据库初始化

#### 4.1.1 登录 MySQL

```bash
mysql -u root -p
# 输入 root 密码
```

#### 4.1.2 执行初始化脚本

```sql
-- 方式一：在 MySQL 命令行内执行
source /path/to/Personnel_Management/backend/src/main/resources/schema.sql;

-- 方式二：命令行直接导入
-- mysql -u root -p < backend/src/main/resources/schema.sql
```

脚本会自动完成：
- 创建数据库 `personnel_management`（utf8mb4 编码）
- 创建 5 张业务表（sys_user / sys_department / sys_employee / salary_record / attendance_record）
- 插入默认管理员账号（admin / admin123，密码 BCrypt 加密）
- 插入示例部门、员工数据

#### 4.1.3 验证数据库

```sql
USE personnel_management;
SHOW TABLES;
-- 应看到 5 张表
SELECT username, role FROM sys_user;
-- 应看到 admin / ADMIN
```

#### 4.1.4 修改后端数据库连接（如需）

编辑 [backend/src/main/resources/application.yml](backend/src/main/resources/application.yml)：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/personnel_management?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: "你的MySQL密码"   # ← 修改此处
```

> 默认配置为 `root / 123456`，生产环境务必修改并使用独立数据库账号。

---

### 4.2 第二步：后端构建与启动

#### 4.2.1 开发模式启动（调试用）

```bash
cd backend

# 直接用 Maven 运行（会自动编译）
mvn clean spring-boot:run
```

#### 4.2.2 生产模式打包

```bash
cd backend

# 打包（跳过测试以加速）
mvn clean package -DskipTests

# 产物：target/personnel-management-1.0.0.jar
```

#### 4.2.3 运行 jar 包

```bash
# 生产环境运行（指定 prod 配置）
java -jar target/personnel-management-1.0.0.jar --spring.profiles.active=prod

# 后台运行（Linux）
nohup java -jar target/personnel-management-1.0.0.jar --spring.profiles.active=prod > startup.log 2>&1 &

# Windows 后台运行
start /b java -jar target/personnel-management-1.0.0.jar > startup.log 2>&1
```

#### 4.2.4 验证后端

后端默认端口 **8080**。启动成功标志：

```
Started PersonnelManagementApplication in x.xxx seconds
Tomcat started on port 8080
```

接口连通性验证：

```bash
# 登录接口测试
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 应返回 JSON：{"code":200, "data":{"token":"..."}, ...}
```

#### 4.2.5 后端关键配置说明（application.yml）

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| `server.port` | 8080 | 后端服务端口 |
| `spring.datasource.*` | root/123456 | 数据库连接 |
| `jwt.secret` | personnel_management_jwt_secret_key_2024 | JWT 签名密钥（生产环境必须修改） |
| `jwt.expiration` | 86400000 | Token 有效期（毫秒，默认 24 小时） |
| `mybatis-plus.configuration.log-impl` | StdOutImpl | 控制台打印 SQL（生产环境建议关闭） |

> 生产环境务必修改 `jwt.secret` 和数据库密码，并关闭 SQL 日志。

---

### 4.3 第三步：前端构建与启动

#### 4.3.1 开发模式启动（调试用）

```bash
cd frontend

# 首次需安装依赖
npm install

# 启动开发服务器（默认端口 5173）
npm run dev
```

开发模式访问：http://localhost:5173

> 开发模式下，Vite 会自动将 `/api` 请求代理到后端 8080 端口（见 `vite.config.js`）。

#### 4.3.2 生产模式打包

```bash
cd frontend

npm install        # 首次或依赖变更时
npm run build
```

产物目录：`frontend/dist/`，包含静态资源（HTML/JS/CSS）。

#### 4.3.3 本地预览构建产物

```bash
npm run preview
# 默认在 4173 端口预览 dist 产物
```

#### 4.3.4 验证前端

浏览器访问 http://localhost:5173（开发）或部署地址，使用默认账号登录：

- 用户名：`admin`
- 密码：`admin123`

应能正常进入系统首页，看到仪表盘数据。

---

### 4.4 第四步：Nginx 部署（生产环境）

#### 4.4.1 部署前端静态资源

```bash
# 将前端构建产物拷贝到 Nginx 站点目录
cp -r frontend/dist/* /usr/share/nginx/html/personnel/
```

#### 4.4.2 Nginx 配置

编辑 Nginx 配置文件（如 `/etc/nginx/conf.d/personnel.conf`）：

```nginx
server {
    listen       80;
    server_name  your-domain.com;

    # 前端静态资源
    location / {
        root   /usr/share/nginx/html/personnel;
        index  index.html;
        # Vue Router history 模式支持
        try_files $uri $uri/ /index.html;
    }

    # 后端 API 反向代理
    location /api/ {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        # 文件上传大小限制（与后端 multipart 一致）
        client_max_body_size 10m;
    }

    # 静态资源缓存
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff2?)$ {
        expires 30d;
        add_header Cache-Control "public, immutable";
    }
}
```

#### 4.4.3 重载 Nginx

```bash
# 测试配置语法
nginx -t

# 平滑重载
nginx -s reload
```

#### 4.4.4 验证 Nginx 部署

浏览器访问 http://your-domain.com，应能正常加载前端页面并完成登录。

---

## 五、完整部署检查清单

部署完成后，按以下清单逐项验证：

- [ ] MySQL 中 `personnel_management` 库已创建，5 张表存在
- [ ] `sys_user` 表存在 admin 账号
- [ ] 后端 `application.yml` 数据库密码已正确配置
- [ ] 后端 `mvn clean package -DskipTests` 打包成功，生成 jar
- [ ] 后端启动日志出现 `Started PersonnelManagementApplication`
- [ ] `curl http://localhost:8080/api/auth/login` 返回 200 与 token
- [ ] 前端 `npm run build` 成功，生成 dist 目录
- [ ] 浏览器访问前端地址可正常登录（admin / admin123）
- [ ] 登录后仪表盘数据正常加载（员工数、部门数等）
- [ ] 各功能模块（员工/部门/薪资/考勤）列表可正常查询
- [ ] Nginx 反代 `/api/` 到 8080 端口正常（生产环境）
- [ ] `jwt.secret` 已修改为生产环境专用密钥
- [ ] SQL 日志（log-impl）已在生产环境关闭

---

## 六、常见问题排查

### 6.1 后端启动失败：MyBatis XML 解析错误

**现象**：启动报 `SAXParseException: 元素内容必须由格式正确的字符数据或标记组成`，定位到某个 `*Mapper.xml`。

**原因**：MyBatis XML 映射文件中 SQL 的 `<`、`>`、`<=`、`>=` 未做 XML 转义。XML 中 `<` 必须转义为 `&lt;`，`>` 建议转义为 `&gt;`。

**解决**：将 SQL 中的比较运算符转义：

```xml
<!-- 错误写法 -->
AND a.attendance_date >= #{startDate}
AND a.attendance_date <= #{endDate}

<!-- 正确写法 -->
AND a.attendance_date &gt;= #{startDate}
AND a.attendance_date &lt;= #{endDate}
```

或使用 `<![CDATA[ ]]>` 包裹：

```xml
<![CDATA[ AND a.attendance_date >= #{startDate} ]]>
```

> 本项目已修复 `AttendanceMapper.xml` 中的 `>=` 转义问题。

### 6.2 前端操作"无反应"（点击确定无响应）

**现象**：新增/编辑弹窗点击"确定"后无任何提示，弹窗不关闭。

**原因排查路径**：
1. **后端是否启动**：打开浏览器 F12 → Network 面板，查看请求是否返回。若请求失败（502/网络错误），多为后端未启动或端口不通。
2. **前端 catch 块是否吞错**：检查提交方法中是否使用空 `catch {}` 吞掉了所有异常。应至少保留 `console.error` 输出，便于定位。
3. **接口返回业务错误**：请求拦截器对非 200 的 `code` 会自动 `ElMessage.error` 提示，若未见提示，检查拦截器逻辑。

**解决**：后端启动后即可恢复；前端 catch 块保留日志输出。

### 6.3 前端登录后接口全部 401

**现象**：登录成功拿到 token，但后续接口全部返回 401 Unauthorized。

**原因**：前端请求拦截器未正确携带 token，或后端 JWT 过滤器配置异常。

**排查**：
- F12 → Network → 查看请求头是否包含 `Authorization: Bearer <token>`
- 检查 `frontend/src/utils/request.js` 请求拦截器是否注入 token
- 检查后端 `SecurityConfig` 是否将登录/白名单接口放行

### 6.4 跨域问题（CORS）

**现象**：浏览器控制台报 `CORS policy` 错误。

**解决**：
- 开发模式：依赖 Vite 的 `proxy` 配置代理 `/api` 到后端，无跨域。
- 生产模式：使用 Nginx 反代 `/api/`，前后端同域，无跨域。
- 若仍出现：后端添加全局 CORS 配置（`@CrossOrigin` 或 `WebMvcConfigurer`）。

### 6.5 数据库连接失败

**现象**：后端启动报 `Communications link failure` 或 `Access denied`。

**排查**：
- 确认 MySQL 服务已启动：`net start mysql`（Windows）/ `systemctl status mysql`（Linux）
- 确认 `application.yml` 中 url、username、password 正确
- 确认数据库 `personnel_management` 已创建
- 确认 MySQL 8 的密码认证插件（caching_sha2_password）连接串包含 `allowPublicKeyRetrieval=true`

### 6.6 PowerShell 执行策略限制

**现象**：Windows 下通过脚本/命令启动后端时报 `PSSecurityException: UnauthorizedAccess`，提示禁止运行脚本。

**解决**（任选其一）：

```powershell
# 方式一：为当前用户放开执行策略（推荐）
Set-ExecutionPolicy -Scope CurrentUser -ExecutionPolicy RemoteSigned

# 方式二：仅当前会话放开
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
```

或直接使用 `mvn` / `java` 命令而非脚本启动。

### 6.7 端口占用

**现象**：启动报 `Port 8080 was already in use`。

**解决**：

```bash
# Windows 查找占用进程
netstat -ano | findstr :8080
taskkill /PID <进程ID> /F

# Linux/Mac
lsof -i:8080
kill -9 <PID>
```

或修改 `application.yml` 的 `server.port`。

---

## 七、安全加固建议（生产环境必读）

1. **修改默认密码**：登录后立即修改 admin 账号密码。
2. **更换 JWT 密钥**：`application.yml` 的 `jwt.secret` 必须替换为高强度随机字符串。
3. **数据库账号最小权限**：为应用创建独立 MySQL 账号，仅授予 `personnel_management` 库的 CRUD 权限。
4. **关闭 SQL 日志**：生产环境将 `mybatis-plus.configuration.log-impl` 注释或改为 `NoLoggingImpl`。
5. **启用 HTTPS**：Nginx 配置 SSL 证书，强制 HTTPS 访问。
6. **日志脱敏**：敏感操作（如薪资查询）日志中避免明文输出工资金额。
7. **定期备份**：对 `personnel_management` 数据库做定时备份（mysqldump 定时任务）。

---

## 八、附录

### 8.1 项目目录结构

```
Personnel_Management/
├── backend/                          # 后端工程
│   ├── src/main/java/com/personnel/
│   │   ├── config/                   # 配置类（Security、MyBatis、Cors 等）
│   │   ├── common/                   # 通用类（Result、ErrorCode、异常处理）
│   │   ├── controller/               # 控制器层
│   │   ├── dto/                      # 数据传输对象
│   │   ├── entity/                   # 实体类
│   │   ├── mapper/                   # 数据访问层
│   │   ├── service/                  # 业务逻辑层
│   │   └── util/                     # 工具类（JWT、加密等）
│   ├── src/main/resources/
│   │   ├── mapper/                   # MyBatis XML 映射文件
│   │   ├── application.yml           # 应用配置
│   │   └── schema.sql                # 数据库初始化脚本
│   ├── pom.xml                       # Maven 依赖
│   └── run.bat                       # Windows 启动脚本
├── frontend/                         # 前端工程
│   ├── src/
│   │   ├── api/                      # API 接口封装
│   │   ├── assets/styles/            # 样式文件
│   │   ├── layout/                   # 布局组件（侧边栏、顶栏）
│   │   ├── router/                   # 路由配置
│   │   ├── store/                    # Pinia 状态管理
│   │   ├── utils/                    # 工具类（request 拦截器等）
│   │   └── views/                    # 页面组件
│   ├── package.json                  # npm 依赖
│   └── vite.config.js                # Vite 配置
├── README.md                         # 项目说明
└── DEPLOY.md                         # 本部署文档
```

### 8.2 API 接口清单

详见 [README.md](README.md#api接口) 的 API 章节，主要接口分组：

- 认证：`/api/auth/*`
- 仪表盘：`/api/dashboard/*`
- 员工：`/api/employees/*`
- 部门：`/api/departments/*`
- 薪资：`/api/salaries/*`
- 考勤：`/api/attendances/*`

### 8.3 默认账号

| 角色 | 用户名 | 密码 | 权限 |
|------|--------|------|------|
| 管理员 | admin | admin123 | 全部功能的增删改查 |
| 普通用户 | （需手动创建） | - | 仅查看，不可增删改 |

---

**文档版本**：v1.0  
**最后更新**：2026-08-01  
**适用项目**：企业人事管理系统 v1.0.0
