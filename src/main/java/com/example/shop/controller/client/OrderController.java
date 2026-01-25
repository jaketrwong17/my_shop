package com.example.shop.controller.client;

import com.example.shop.domain.Cart;
import com.example.shop.domain.CartItem;
import com.example.shop.domain.User;
import com.example.shop.domain.Voucher;
import com.example.shop.service.OrderService;
import com.example.shop.service.ProductService;
import com.example.shop.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller("clientOrderController")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;
    private final ProductService productService;

    public OrderController(OrderService orderService, UserService userService, ProductService productService) {
        this.orderService = orderService;
        this.userService = userService;
        this.productService = productService;
    }

    @GetMapping("/checkout")
    public String getCheckoutPage(Model model, HttpServletRequest request,
            @RequestParam(required = false) List<Long> selectedIds,
            @RequestParam(required = false) String voucherCode) {

        HttpSession session = request.getSession(false);
        String email = (String) session.getAttribute("email");
        if (email == null)
            return "redirect:/login";

        User user = this.userService.getUserByEmail(email);
        Cart cart = this.productService.fetchCartByUserEmail(email);

        // Logic kiểm tra giỏ hàng
        if (cart == null || cart.getCartItems().isEmpty() || selectedIds == null || selectedIds.isEmpty()) {
            return "redirect:/cart";
        }

        List<CartItem> displayItems = new ArrayList<>();
        for (CartItem item : cart.getCartItems()) {
            if (selectedIds.contains(item.getId()))
                displayItems.add(item);
        }

        double originalPrice = 0;
        for (CartItem item : displayItems)
            originalPrice += item.getPrice() * item.getQuantity();

        // Logic Voucher
        double discountAmount = 0;
        Voucher voucher = null;
        Map<Long, Double> discountMap = new HashMap<>();

        if (voucherCode != null && !voucherCode.isEmpty()) {
            voucher = this.orderService.getVoucherByCode(voucherCode);
            if (voucher != null) {
                discountMap = this.orderService.calculateDiscountBreakdown(displayItems, voucher);
                for (Double val : discountMap.values())
                    discountAmount += val;
                model.addAttribute("voucher", voucher);
                model.addAttribute("successMessage", "Đã áp dụng mã: " + voucher.getCode());
            } else {
                model.addAttribute("errorMessage", "Mã giảm giá không hợp lệ!");
            }
        }

        double totalPrice = originalPrice - discountAmount;
        if (totalPrice < 0)
            totalPrice = 0;

        model.addAttribute("displayItems", displayItems);
        model.addAttribute("originalPrice", originalPrice);
        model.addAttribute("discountAmount", discountAmount);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("user", user);
        model.addAttribute("selectedIds", selectedIds);
        model.addAttribute("voucherCode", voucherCode);
        model.addAttribute("discountMap", discountMap);

        return "client/cart/checkout";
    }

    // ==================== HÀM DEBUG QUAN TRỌNG ====================
    @PostMapping("/place-order")
    public String handlePlaceOrder(
            @RequestParam("receiverName") String receiverName,
            @RequestParam("receiverAddress") String receiverAddress,
            @RequestParam("receiverPhone") String receiverPhone,
            @RequestParam("paymentMethod") String paymentMethod,
            // Sửa required = false để tránh lỗi 400 Bad Request nếu list bị rỗng
            @RequestParam(value = "cartItemIds", required = false) List<Long> cartItemIds,
            @RequestParam(value = "voucherCode", required = false) String voucherCode,
            HttpServletRequest request) {

        // --- BẮT ĐẦU DEBUG: In ra Console xem code có chạy vào đây không ---
        System.out.println(">>> DEBUG: Đang xử lý đặt hàng...");
        System.out.println(">>> Người nhận: " + receiverName);
        System.out.println(">>> Danh sách ID sản phẩm: " + cartItemIds);

        // Kiểm tra lỗi dữ liệu đầu vào
        if (cartItemIds == null || cartItemIds.isEmpty()) {
            System.out.println(">>> LỖI: Không tìm thấy sản phẩm nào (cartItemIds bị null hoặc rỗng)");
            return "redirect:/cart?error=Vui long chon san pham de thanh toan";
        }

        HttpSession session = request.getSession(false);
        String email = (String) session.getAttribute("email");
        if (email == null)
            return "redirect:/login";

        User user = this.userService.getUserByEmail(email);

        try {
            this.orderService.handlePlaceOrder(user, session, receiverName, receiverAddress, receiverPhone,
                    paymentMethod, cartItemIds, voucherCode);
            System.out.println(">>> THÀNH CÔNG: Đã đặt hàng xong, chuẩn bị chuyển hướng");
        } catch (Exception e) {
            // In toàn bộ lỗi ra console để bạn đọc
            System.out.println(">>> LỖI XẢY RA TRONG SERVICE:");
            e.printStackTrace();
            return "redirect:/checkout?error=" + e.getMessage();
        }

        return "redirect:/thanks";
    }

    @GetMapping("/thanks")
    public String getThankYouPage() {
        return "client/cart/thanks";
    }
}