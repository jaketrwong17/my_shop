package com.example.shop.controller.admin;

import com.example.shop.domain.Order;
import com.example.shop.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

// [SỬA LẠI DÒNG NÀY] Đặt tên bean để không trùng với client
@Controller("adminOrderController")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/admin/order")
    public String getDashboard(Model model) {
        // Bây giờ hàm này sẽ hết lỗi vì Service đã có getAllOrders()
        List<Order> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
        return "admin/order/show";
    }

    @GetMapping("/admin/order/view/{id}")
    public String getOrderDetailPage(Model model, @PathVariable long id) {
        Optional<Order> orderOptional = orderService.getOrderById(id);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            model.addAttribute("order", order);
            model.addAttribute("orderDetails", order.getOrderDetails());
            return "admin/order/detail";
        } else {
            return "redirect:/admin/order";
        }
    }

    @PostMapping("/admin/order/update/{id}")
    public String updateOrderStatus(@PathVariable long id, @RequestParam("status") String status) {
        Optional<Order> orderOptional = orderService.getOrderById(id);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            order.setStatus(status);
            orderService.handleSaveOrder(order);
        }
        return "redirect:/admin/order";
    }

    @GetMapping("/admin/order/delete/{id}")
    public String deleteOrder(@PathVariable long id) {
        orderService.deleteOrderById(id);
        return "redirect:/admin/order";
    }
}