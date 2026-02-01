package com.example.shop.service;

import com.example.shop.domain.Cart;
import com.example.shop.domain.Role;
import com.example.shop.domain.User;
import com.example.shop.domain.dto.RegisterDTO;
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

    // Lưu thông tin người dùng vào cơ sở dữ liệu
    public User handleSaveUser(User user) {
        return userRepository.save(user);
    }

    // Đăng ký người dùng mới, gán quyền mặc định và mã hóa mật khẩu
    public User registerUser(User user) {
        Role role = roleRepository.findByName("USER");
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return this.handleSaveUser(user);
    }

    // Tìm kiếm người dùng theo địa chỉ email
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Lấy danh sách tất cả người dùng trong hệ thống
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Tìm kiếm người dùng theo ID
    public User getUserById(long id) {
        return this.userRepository.findById(id).orElse(null);
    }

    // Đảo trạng thái khóa/mở khóa tài khoản người dùng
    public void toggleLockUser(long id) {
        User user = this.getUserById(id);
        if (user != null) {
            user.setIsLocked(!user.getIsLocked());
            this.userRepository.save(user);
        }
    }

    // Xóa người dùng và gỡ bỏ liên kết với giỏ hàng
    public void deleteUserById(long id) {
        User user = this.getUserById(id);
        if (user != null) {
            if (user.getCart() != null) {
                user.setCart(null);
            }
            userRepository.deleteById(id);
        }
    }

    // Lấy thông tin quyền hạn theo tên role
    public Role getRoleByName(String name) {
        return this.roleRepository.findByName(name);
    }

    // Chuyển đổi dữ liệu từ RegisterDTO sang thực thể User
    public User registerDTOtoUser(RegisterDTO registerDTO) {
        User user = new User();
        user.setFullName(registerDTO.getFirstName() + " " + registerDTO.getLastName());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(registerDTO.getPassword());
        return user;
    }

    // Kiểm tra xem email đã tồn tại trong hệ thống chưa
    public boolean checkEmailExists(String email) {
        return this.userRepository.existsByEmail(email);
    }

    // Cập nhật thông tin cá nhân của người dùng
    public void updateUserProfile(long userId, User updatedUser) {
        User currentUser = this.getUserById(userId);
        if (currentUser != null) {
            currentUser.setFullName(updatedUser.getFullName());
            currentUser.setPhone(updatedUser.getPhone());
            currentUser.setAddress(updatedUser.getAddress());
            this.userRepository.save(currentUser);
        }
    }

    // Đếm tổng số lượng người dùng trong hệ thống
    public long countAllUsers() {
        return userRepository.count();
    }
}