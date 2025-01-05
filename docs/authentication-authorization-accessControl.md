## Trong Spring Security, các khái niệm Access Control và Authorization được thể hiện và triển khai như sau:

## 1. Authentication (Xác thực):

Spring Security sử dụng interface AuthenticationManager và các implementation của nó để xác thực người dùng. Quá trình
này thường bao gồm:

- UsernamePasswordAuthenticationFilter để xử lý form đăng nhập.
- AuthenticationProvider để kiểm tra thông tin đăng nhập.
  UserDetailsService để tải thông tin người dùng.

Ví dụ cấu hình cơ bản:

```java

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }
}
```

## 2. Authorization (Ủy quyền):

Spring Security sử dụng AccessDecisionManager để quyết định liệu một người dùng đã xác thực có được phép truy cập một
tài nguyên cụ thể hay không. Điều này thường được cấu hình thông qua:

- antMatchers() để xác định URL patterns
- hasRole(), hasAuthority() để xác định quyền cần thiết

Ví dụ:

```java

@Override
protected void configure(HttpSecurity http) throws Exception {
    http
            .authorizeRequests()
            .antMatchers("/", "/home").permitAll()
            .antMatchers("/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated()
            .and()
            .formLogin()
            .loginPage("/login").permitAll()
            .and()
            .logout().permitAll();
}
```

## 3.Access Control (Kiểm soát truy cập):

Spring Security cung cấp nhiều cách để kiểm soát truy cập chi tiết:

#### a. Method Security:

Sử dụng annotations như @PreAuthorize, @PostAuthorize để kiểm soát truy cập ở cấp độ phương thức.

```java

@PreAuthorize("hasRole('ADMIN')")
public void deleteUser(Long userId) {
    // Chỉ ADMIN mới có thể xóa user
}
```

#### b. ACL (Access Control List):

Cho phép kiểm soát truy cập chi tiết đến từng đối tượng cụ thể trong ứng dụng.
c. Expression-based access control: Sử dụng SpEL (Spring Expression Language) để định nghĩa các quy tắc phức tạp.

```java

@PreAuthorize("hasRole('ADMIN') or #user.id == authentication.principal.id")
public void updateUser(User user) {
    // Chỉ ADMIN hoặc chính user đó mới có thể cập nhật thông tin
}
```

## 4. Custom Authorization:

Spring Security cho phép tùy chỉnh logic authorization bằng cách implement các interface như PermissionEvaluator hoặc
tạo custom voters cho AccessDecisionManager.

## Summary:

Tóm lại, Spring Security cung cấp một framework linh hoạt để triển khai cả Authentication, Authorization và Access
Control. Nó cho phép kiểm soát từ mức coarse-grained (URL-based) đến fine-grained (method-level và object-level), đáp
ứng các yêu cầu bảo mật phức tạp trong ứng dụng enterprise.