Certainly! Here’s a professional README for the Spring Security project:

---

# Spring Security Project

This repository contains a Spring Security project designed to demonstrate secure authentication and authorization practices in a Spring Boot application. The project is structured to follow best practices in Java development and utilizes Maven for dependency management.

## Features

- **User Authentication**: Secure login using Spring Security.
- **Role-Based Authorization**: Access control based on user roles.
- **JWT Integration**: Secure token-based authentication.
- **Database Integration**: User data management with JPA and Hibernate.
- **RESTful API**: Secure endpoints with role-based access.

## Prerequisites

- **Java 11+**
- **Maven 3.6+**
- **Spring Boot 2.5+**
- **MySQL/PostgreSQL** (optional for database integration)

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/nguyenthuanit265/spring-security-project.git
   ```
2. Navigate to the project directory:
   ```bash
   cd spring-security-project
   ```
3. Build the project:
   ```bash
   mvn clean install
   ```
4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

## Configuration

Modify the `application.properties` file in the `src/main/resources` directory to configure database connections, security settings, and other application parameters.

## Usage

Access the application via `http://localhost:8080`. Use the provided API endpoints for authentication and user management.

# Mô tả chi tiết dự án Spring Security API Demo

## 1. Tổng quan

Dự án này là một demo nhỏ về việc triển khai bảo mật cho REST API sử dụng Spring Security. Nó minh họa cách xác thực người dùng, quản lý phiên đăng nhập bằng JWT (JSON Web Tokens), và kiểm soát quyền truy cập vào các tài nguyên dựa trên vai trò của người dùng.

## 2. Công nghệ sử dụng

- Spring Boot
- Spring Security
- JSON Web Tokens (JWT)
- RESTful API
- Java

## 3. Cấu trúc API

### 3.1 Endpoints công khai

- `GET /api/v1/resources/public`: Truy cập tài nguyên công khai
- `GET /api/v1/public`: Một endpoint công khai khác

### 3.2 Endpoints xác thực

- `POST /api/v1/auth/sign-up`: Đăng ký người dùng mới
- `POST /api/v1/auth/login`: Đăng nhập và nhận token JWT

### 3.3 Endpoints người dùng

- `GET /api/v1/users/content`: Lấy nội dung dành cho người dùng đã xác thực
- `GET /api/v1/users/{id}`: Lấy thông tin profile của người dùng theo ID

### 3.4 Endpoints admin

- `GET /api/v1/admins/profiles`: Lấy thông tin profile của admin
- `GET /api/v1/admins/board`: Truy cập bảng điều khiển admin

## 4. Luồng xác thực và ủy quyền

1. Người dùng đăng ký tài khoản thông qua endpoint đăng ký.
2. Người dùng đăng nhập và nhận được JWT.
3. Các yêu cầu tiếp theo đến các endpoint được bảo vệ phải bao gồm JWT trong header "Authorization".
4. Server xác thực JWT và kiểm tra quyền của người dùng trước khi cho phép truy cập vào tài nguyên.

## 5. Bảo mật

- Sử dụng JWT để quản lý phiên đăng nhập, tránh lưu trữ thông tin phiên trên server.
- Phân quyền dựa trên vai trò (ROLE_USER, ROLE_ADMIN).
- Mã hóa mật khẩu trước khi lưu vào cơ sở dữ liệu.
- Sử dụng HTTPS để bảo vệ dữ liệu trong quá trình truyền tải (được giả định).

## 6. Xử lý lỗi

- Trả về mã lỗi HTTP phù hợp (ví dụ: 403 Forbidden) khi truy cập không được phép.
- Cung cấp thông báo lỗi chi tiết trong response body.
