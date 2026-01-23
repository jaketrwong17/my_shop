package com.example.shop.controller.admin;

import com.example.shop.domain.User;
import com.example.shop.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.shop.domain.Order; // <-- Thêm dòng này
import java.util.List; // <-- Thêm dòng này nếu List cũng bị đỏ

import java.util.List;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 1. TÌM KIẾM & HIỂN THỊ DANH SÁCH
    @GetMapping("/admin/user")
    public String getUserPage(Model model, @RequestParam(value = "keyword", required = false) String keyword) {
        // Giả sử Jake đã có hàm getAllUsers(keyword) trong service (giống bên Product)
        // Nếu chưa thì dùng getAllUsers() tạm
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        model.addAttribute("keyword", keyword);
        return "admin/user/show";
    }

    // 2. KHÓA / MỞ KHÓA TÀI KHOẢN
    @PostMapping("/admin/user/lock/{id}")
    public String lockUser(@PathVariable long id) {
        userService.toggleLockUser(id);
        return "redirect:/admin/user";
    }

    // 3. XÓA TÀI KHOẢN
    @GetMapping("/admin/user/delete/{id}")
    public String deleteUser(@PathVariable long id) {
        User user = userService.getUserById(id);
        if (user != null) {
            List<Order> orders = user.getOrders();
            boolean hasActiveOrder = false;

            if (orders != null) {
                for (Order order : orders) {
                    // Chặn nếu có đơn chưa xong
                    if ("PENDING".equals(order.getStatus()) || "SHIPPING".equals(order.getStatus())) {
                        hasActiveOrder = true;
                        break;
                    }
                }
            }

            if (hasActiveOrder) {
                // Thông báo hoặc redirect nếu có đơn dở
                return "redirect:/admin/user?error=active_order";
            } else {
                // Chỉ xóa khi mọi thứ đã an toàn
                userService.deleteUserById(id);
            }
        }
        return "redirect:/admin/user";
    }
}