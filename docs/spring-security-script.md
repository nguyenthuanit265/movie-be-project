 - Docker run MYSQL
 - Tạo database
 - Tạo class mapping entity - User, Role
 - Tạo UserRepository
 - Tạo UserService + Impl
 - Tạo DTO Request AuthRequest, SignUpRequest
 - Tạo AuthController
 - Cấu hình filterChain, tạo CustomUserDetailsServiceImpl

 - Viết class JwtTokenUtils -> validate token, generate token,...

OAuth2 Resource Server và sự khác biệt của nó so với cách xác thực token truyền thống.
Mục đích chính của OAuth2 Resource Server:
    
    Bảo vệ tài nguyên: Nó bảo vệ các API và dữ liệu của bạn, chỉ cho phép truy cập khi có token hợp lệ.
    Tách biệt trách nhiệm: Nó tách biệt việc xác thực và ủy quyền ra khỏi logic nghiệp vụ của ứng dụng.
    Hỗ trợ xác thực phân tán: Cho phép nhiều ứng dụng hoặc dịch vụ sử dụng cùng một hệ thống xác thực.
    
    Sự khác biệt với cách generate token truyền thống:
    
    Cấu trúc và quy trình:
    
    Truyền thống:
    
    Ứng dụng tự tạo và quản lý token.
    Thường sử dụng một secret key duy nhất để ký và xác thực token.
    
    
    OAuth2 Resource Server:
    
    Token được tạo bởi một Authorization Server riêng biệt.
    Resource Server chỉ xác thực token, không tạo ra chúng.
    Sử dụng cặp khóa công khai/riêng tư cho việc ký và xác thực token.
    
    
    
    
    Khả năng mở rộng:
    
    Truyền thống:
    
    Khó mở rộng khi có nhiều ứng dụng hoặc dịch vụ.
    Mỗi ứng dụng cần quản lý việc tạo và xác thực token riêng.
    
    
    OAuth2 Resource Server:
    
    Dễ dàng mở rộng cho nhiều ứng dụng và dịch vụ.
    Một Authorization Server có thể cấp token cho nhiều Resource Server.
    
    
    
    
    Bảo mật:
    
    Truyền thống:
    
    Nếu secret key bị lộ, toàn bộ hệ thống có thể bị xâm phạm.
    Khó thu hồi hoặc vô hiệu hóa token cụ thể.
    
    
    OAuth2 Resource Server:
    
    Sử dụng cặp khóa công khai/riêng tư, an toàn hơn.
    Hỗ trợ thu hồi token và kiểm tra trạng thái token (token introspection).
    
    
    
    
    Tiêu chuẩn hóa:
    
    Truyền thống:
    
    Thường là giải pháp tùy chỉnh, khó tích hợp với các hệ thống khác.
    
    
    OAuth2 Resource Server:
    
    Tuân theo tiêu chuẩn OAuth2, dễ dàng tích hợp với các hệ thống khác.
    Hỗ trợ nhiều loại grant type (ví dụ: authorization code, client credentials).
    
    
    
    
    Phân quyền:
    
    Truyền thống:
    
    Thường chỉ hỗ trợ phân quyền đơn giản (ví dụ: dựa trên vai trò).
    
    
    OAuth2 Resource Server:
    
    Hỗ trợ phân quyền phức tạp hơn thông qua scopes và claims trong token.
    
    
    
    
    Tương thích với Single Sign-On (SSO):
    
    Truyền thống:
    
    Khó triển khai SSO giữa nhiều ứng dụng.
    
    
    OAuth2 Resource Server:
    
    Dễ dàng tích hợp với các giải pháp SSO dựa trên OAuth2/OpenID Connect.