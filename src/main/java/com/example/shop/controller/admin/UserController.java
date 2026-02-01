package com.example.shop.controller.admin;

import com.example.shop.domain.User;
import com.example.shop.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.shop.domain.Order;
import java.util.List;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Tìm kiếm và hiển thị danh sách người dùng
    @GetMapping("/admin/user")
    public String getUserPage(Model model, @RequestParam(value = "keyword", required = false) String keyword) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        model.addAttribute("keyword", keyword);
        return "admin/user/show";
    }

    // Xử lý khóa hoặc mở khóa tài khoản người dùng
    @PostMapping("/admin/user/lock/{id}")
    public String lockUser(@PathVariable long id) {
        userService.toggleLockUser(id);
        return "redirect:/admin/user";
    }

    // Xử lý xóa tài khoản người dùng sau khi kiểm tra trạng thái đơn hàng
    @GetMapping("/admin/user/delete/{id}")
    public String deleteUser(@PathVariable long id) {
        User user = userService.getUserById(id);
        if (user != null) {
            List<Order> orders = user.getOrders();
            boolean hasActiveOrder = false;

            if (orders != null) {
                for (Order order : orders) {
                    if ("PENDING".equals(order.getStatus()) || "SHIPPING".equals(order.getStatus())) {
                        hasActiveOrder = true;
                        break;
                    }
                }
            }

            if (hasActiveOrder) {
                return "redirect:/admin/user?error=active_order";
            } else {
                userService.deleteUserById(id);
            }
        }
        return "redirect:/admin/user";
    }
}