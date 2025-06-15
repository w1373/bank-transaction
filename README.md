# 银行交易系统

## 项目依赖

### 核心依赖库

- **Spring Boot Starter Web** (`spring-boot-starter-web`)
  - 提供全栈Web开发支持，包含嵌入式Tomcat和Spring MVC

- **Spring Boot Starter Cache** (`spring-boot-starter-cache`)
  - 支持多种缓存实现（JCache、EhCache、Redis等）的抽象层

- **Spring Boot Validation** (`spring-boot-starter-validation`)
  - 提供声明式数据验证支持（基于Hibernate Validator）

- **Thymeleaf** (`spring-boot-starter-thymeleaf`)
  - 现代化服务端Java模板引擎，支持HTML5

- **Lombok** (`lombok`)
  - 通过注解自动生成getter/setter/constructor等样板代码

- **Jackson Databind** (`jackson-databind`)
  - 高性能JSON处理库，支持对象与JSON的序列化/反序列化

### 测试依赖

- **Mockito Core** (`mockito-core`)
  - 流行的Java mocking框架，用于单元测试

- **JUnit Jupiter** (`junit-jupiter`)
  - 新一代JUnit测试框架（JUnit 5+）

## 快速启动

```bash
mvn spring-boot:run
```

访问地址：http://localhost:8080