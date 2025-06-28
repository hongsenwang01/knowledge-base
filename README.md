# 文件管理系统后端服务

## 项目介绍

本项目是基于Spring Boot + MyBatis Plus + MySQL的文件管理系统后端服务，支持目录管理、文件上传、检索和下载功能。

## 技术栈

- **后端框架**: Spring Boot 3.3.2
- **数据库框架**: MyBatis Plus 3.5.5
- **数据库**: MySQL 8.0+
- **Java版本**: JDK 17
- **构建工具**: Maven

## 项目结构

```
src/main/java/com/example/filemanager/
├── FileManagerApplication.java     # 启动类
├── controller/                     # 控制器层
├── service/                        # 服务层
│   └── impl/                      # 服务实现类
├── mapper/                        # 数据访问层
├── entity/                        # 实体类
├── dto/                           # 数据传输对象
│   ├── request/                   # 请求DTO
│   └── response/                  # 响应DTO
├── vo/                            # 视图对象
├── config/                        # 配置类
│   ├── MyBatisPlusConfig.java     # MyBatis Plus配置
│   └── FileStorageConfig.java     # 文件存储配置
├── common/                        # 公共类
│   ├── result/                    # 统一返回结果
│   ├── exception/                 # 异常处理
│   ├── constant/                  # 常量定义
│   └── util/                      # 工具类
└── enums/                         # 枚举类

src/main/resources/
├── application.yml                # 主配置文件
├── application-dev.yml            # 开发环境配置
├── application-prod.yml           # 生产环境配置
├── mapper/                        # MyBatis XML映射文件
└── db/migration/                  # 数据库脚本
    └── V1__init_tables.sql       # 初始化脚本
```

## 快速开始

### 1. 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+

### 2. 数据库准备

执行 `src/main/resources/db/migration/V1__init_tables.sql` 中的SQL脚本创建数据库和表。

### 3. 配置文件

修改 `application-dev.yml` 中的数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/file_manager?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
    username: your_username
    password: your_password
```

### 4. 启动项目

```bash
mvn spring-boot:run
```

项目启动后，访问：http://localhost:8080/api/v1

## API文档

### 文件管理接口

- `GET /api/v1/files` - 获取文件列表
- `GET /api/v1/files/{id}` - 获取文件详情
- `POST /api/v1/files/upload` - 上传文件
- `DELETE /api/v1/files/{id}` - 删除文件
- `GET /api/v1/files/{id}/download` - 下载文件

### 目录管理接口

- `GET /api/v1/directories` - 获取目录列表
- `POST /api/v1/directories` - 创建目录
- `PUT /api/v1/directories/{id}` - 更新目录
- `DELETE /api/v1/directories/{id}` - 删除目录

### 文件搜索接口

- `GET /api/v1/search/files` - 搜索文件

## 开发规范

请参考项目根目录下的 `文件管理系统开发规范.md` 文件。

## 数据库设计

### 主要表结构

1. **directories** - 目录表
2. **file_info** - 文件信息表
3. **file_directory_relations** - 文件目录关系表

详细的表结构请参考 `V1__init_tables.sql` 文件。

## 部署说明

### Docker部署

```bash
# 构建镜像
docker build -t file-manager:latest .

# 运行容器
docker run -d -p 8080:8080 \
  -e DB_HOST=mysql_host \
  -e DB_PORT=3306 \
  -e DB_NAME=file_manager \
  -e DB_USERNAME=root \
  -e DB_PASSWORD=password \
  -v /data/files:/data/files \
  file-manager:latest
```

### 传统部署

1. 打包项目：`mvn clean package`
2. 上传jar包到服务器
3. 配置环境变量
4. 启动服务：`java -jar file-manager-0.0.1-SNAPSHOT.jar`

## 注意事项

1. 文件存储路径需要确保应用有读写权限
2. MySQL版本建议8.0以上
3. 生产环境请修改默认密码和配置
4. 建议配置Nginx作为反向代理

## 联系方式

如有问题请联系开发团队。 