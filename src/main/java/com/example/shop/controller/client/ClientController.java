package com.example.shop.controller.client;

import com.example.shop.domain.User;
import com.example.shop.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ClientController {

    private final UserService userService;

    public ClientController(UserService userService) {
        this.userService = userService;
    }

    // 1. Hiển thị trang thông tin cá nhân
    @GetMapping("/profile")
    public String getProfilePage(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String email = (String) session.getAttribute("email"); // Lấy email từ session

        User currentUser = this.userService.getUserByEmail(email);
        model.addAttribute("user", currentUser);

        return "client/profile/show";
    }

    // 2. Xử lý cập nhật thông tin
    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute("user") User user, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String email = (String) session.getAttribute("email");

        // Lấy ID của người đang đăng nhập để đảm bảo an toàn (không tin tưởng ID từ
        // form gửi lên hoàn toàn)
        User currentUser = this.userService.getUserByEmail(email);

        // Gọi service cập nhật
        this.userService.updateUserProfile(currentUser.getId(), user);

        // Cập nhật lại thông tin hiển thị trên Session (nếu có lưu tên hiển thị)
        session.setAttribute("fullName", user.getFullName()); // Nếu bạn dùng fullName trên header

        return "redirect:/profile";
    }
}