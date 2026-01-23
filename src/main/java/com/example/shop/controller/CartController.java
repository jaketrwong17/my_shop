package com.example.shop.controller;

import com.example.shop.domain.Cart;
import com.example.shop.domain.User;
import com.example.shop.service.OrderService;
import com.example.shop.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CartController {

    private final OrderService orderService;
    private final UserService userService;

    public CartController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    // 1. Xem giỏ hàng
    @GetMapping("/cart")
    public String getCartPage(Model model, HttpServletRequest request) {
        // Giả sử đã có User trong Session sau khi login
        // User currentUser = ... (Lấy từ Session hoặc SecurityContext)
        // model.addAttribute("cart", ...);
        return "client/cart";
    }

    // 2. Thêm sản phẩm vào giỏ (Logic này Jake có thể viết thêm ở CartService)
    @PostMapping("/add-to-cart/{id}")
    public String addProductToCart(@PathVariable long id, HttpServletRequest request) {
        // Logic thêm vào giỏ...
        return "redirect:/";
    }

    // 3. Xử lý ĐẶT HÀNG (Checkout)
    @PostMapping("/place-order")
    public String handlePlaceOrder(HttpServletRequest request,
            @RequestParam("receiverName") String receiverName,
            @RequestParam("receiverAddress") String receiverAddress,
            @RequestParam("receiverPhone") String receiverPhone,
            @RequestParam("paymentMethod") String paymentMethod) {

        // Bước 1: Lấy User đang đăng nhập (Giả sử dùng Spring Security hoặc Session)
        // HttpSession session = request.getSession();
        // String email = (String) session.getAttribute("email");
        // User user = userService.getUserByEmail(email);

        // Demo tạm: Lấy User có ID = 1 để test
        User user = userService.getUserByEmail("test@gmail.com");

        // Bước 2: Lấy giỏ hàng của User đó
        // Cart cart = cartRepository.findByUser(user);
        Cart cart = user.getCart(); // Cần đảm bảo User entity có mapping getCart()

        return "redirect:/thank-you";
    }

    @GetMapping("/thank-you")
    public String getThankYouPage() {
        return "client/thank-you";
    }
}