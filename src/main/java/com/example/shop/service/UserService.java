package com.example.shop.service;

import com.example.shop.domain.Cart;
import com.example.shop.domain.Role;
import com.example.shop.domain.User;
import com.example.shop.repository.RoleRepository;
import com.example.shop.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 1. Xử lý lưu user (Dùng chung cho tạo mới và cập nhật)
    public User handleSaveUser(User user) {
        return userRepository.save(user);
    }

    // 2. Đăng ký user mới (Chỉ mã hóa mật khẩu và gán role khi tạo mới)
    public User registerUser(User user) {
        Role role = roleRepository.findByName("USER"); // Đổi thành USER cho đúng kịch bản khách hàng
        user.setRole(role);

        // Chỉ mã hóa mật khẩu ở bước đăng ký
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return this.handleSaveUser(user);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(long id) {
        return this.userRepository.findById(id).orElse(null);
    }

    // [FIX LỖI MẤT ROLE KHI KHÓA]
    public void toggleLockUser(long id) {
        User user = this.getUserById(id);
        if (user != null) {
            user.setIsLocked(!user.getIsLocked());
            // Dùng trực tiếp repository.save để tránh đi qua logic mã hóa lại pass
            this.userRepository.save(user);
        }
    }

    public void deleteUserById(long id) {
        // [CẬP NHẬT] Gỡ liên kết Cart trước khi xóa để tránh lỗi
        // TransientObjectException
        User user = this.getUserById(id);
        if (user != null) {
            if (user.getCart() != null) {
                user.setCart(null);
            }
            userRepository.deleteById(id);
        }
    }

    // [BỔ SUNG] Lấy Role theo tên cho các logic phân quyền
    public Role getRoleByName(String name) {
        return this.roleRepository.findByName(name);
    }
}