package com.example.shop.controller.client;

import com.example.shop.domain.User;
import com.example.shop.service.CategoryService;
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
    private final CategoryService categoryService;

    public ClientController(UserService userService, CategoryService categoryService) {
        this.userService = userService;
        this.categoryService = categoryService;
    }

    // 1. Hiển thị trang thông tin cá nhân
    @GetMapping("/profile")
    public String getProfilePage(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String email = (String) session.getAttribute("email"); // Lấy email từ session

        User currentUser = this.userService.getUserByEmail(email);
        model.addAttribute("user", currentUser);
        model.addAttribute("categories", categoryService.getAllCategories(null));
        return "client/profile/show";
    }

    // 2. Xử lý cập nhật thông tin
    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute("user") User user, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String email = (String) session.getAttribute("email");

        User currentUser = this.userService.getUserByEmail(email);

        this.userService.updateUserProfile(currentUser.getId(), user);

        session.setAttribute("fullName", user.getFullName());

        return "redirect:/profile";
    }
}