package com.example.shop.repository;

import com.example.shop.domain.User;
import com.example.shop.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 1. Tìm user bằng email (Dùng cho chức năng Đăng nhập)
    User findByEmail(String email);

    // 2. Kiểm tra email đã tồn tại chưa (Dùng cho Validate Đăng ký)
    boolean existsByEmail(String email);

    // 3. Tìm danh sách user theo Role [Sửa lại từ comment cũ của bạn]
    // Spring Data JPA sẽ tự động hiểu việc tìm theo đối tượng Role liên kết
    List<User> findByRole(Role role);

    // 4. Tìm kiếm người dùng theo tên hoặc email (Phục vụ chức năng Search bên
    // Admin)
    List<User> findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name, String email);
}