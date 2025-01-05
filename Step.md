### Buoi 1

- Tạo database bằng docker

```
jdbc:mysql://localhost:3306/db_spring_security?allowPublicKeyRetrieval=true&useSSL=false

- docker run --name some-mysql -e MYSQL_ROOT_PASSWORD=123456 -e MYSQL_DATABASE=db_spring_security -d -p 3306:3306 -v mysql-data:/var/lib/mysql mysql

```

- Config

```
server:
  port: 8081
spring:
  application:
    name: spring-security-practice-2
  datasource:
    url: jdbc:mysql://localhost:3306/db_spring_security
    username: root
    password: 123456
  jpa:
    show-sql: true
    hibernate:
      ddl-auto: update
```

- Define file application.yml: port, url datasource, username, password, jpa config
- Tạo entity User, Role
- Mapping entity class + table
- Cấu hình Spring Security
- Viết API: /sign-up, /login, /admins/profile, users/profile
- Sử dụng Basic Auth

### Buoi 2

- Tạo UserRepository
- Tạo UserService + Impl
- Tạo DTO Request AuthRequest, SignUpRequest
- Implement API /sign-up, /login -> trả về thông tin user đăng nhập
- Cấu hình filterChain, tạo CustomUserDetailsServiceImpl, AuthenticationManager, Provider

### Buoi 3

- Viết class JwtTokenUtils -> validate token, generate token,...
- Implement API /login return JWT
- Phân quyền trên URL
- Phân quyền trên method