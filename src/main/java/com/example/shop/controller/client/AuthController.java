package com.example.shop.controller.client;

import com.example.shop.domain.User;
import com.example.shop.domain.dto.RegisterDTO;
import com.example.shop.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "client/auth/login";
    }

    @GetMapping("/register")
    public String getRegisterPage(Model model) {
        model.addAttribute("registerUser", new RegisterDTO());
        return "client/auth/register";
    }

    @PostMapping("/register")
    public String handleRegister(@ModelAttribute("registerUser") RegisterDTO registerDTO, Model model) {

        // 1. Kiểm tra Email đã tồn tại chưa
        if (userService.checkEmailExists(registerDTO.getEmail())) {
            model.addAttribute("error", "Email này đã được sử dụng, vui lòng chọn email khác!");
            model.addAttribute("registerUser", registerDTO); // Trả lại dữ liệu để không phải nhập lại
            return "client/auth/register"; // Quay lại trang đăng ký
        }

        // 2. Kiểm tra mật khẩu xác nhận (Confirm Password)
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            model.addAttribute("error", "Mật khẩu xác nhận không khớp!");
            model.addAttribute("registerUser", registerDTO);
            return "client/auth/register";
        }

        // 3. Logic đăng ký thành công (như cũ)
        User user = userService.registerDTOtoUser(registerDTO);
        String hashPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);
        user.setRole(this.userService.getRoleByName("USER"));

        this.userService.handleSaveUser(user);
        return "redirect:/login";
    }
}