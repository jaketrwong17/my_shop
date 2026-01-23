package com.example.shop.repository;

import com.example.shop.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    // Tìm Role theo tên (VD: "ADMIN", "CUSTOMER")
    // Dùng để gán quyền mặc định khi đăng ký
    Role findByName(String name);
}