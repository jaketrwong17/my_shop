package com.example.shop.controller.client;

import com.example.shop.domain.Cart;
import com.example.shop.domain.CartItem;
import com.example.shop.domain.User;
import com.example.shop.domain.Voucher;
import com.example.shop.service.CategoryService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Optional;
import com.example.shop.domain.Order;

@Controller("clientOrderController")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;
    private final ProductService productService;
    private final CategoryService categoryService;

    public OrderController(OrderService orderService, UserService userService,
            ProductService productService, CategoryService categoryService) {
        this.orderService = orderService;
        this.userService = userService;
        this.productService = productService;
        this.categoryService = categoryService;
    }

    // Trang thanh toán
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
        model.addAttribute("categories", categoryService.getAllCategories(null));

        return "client/cart/checkout";
    }

    // Xử lý đặt hàng
    @PostMapping("/place-order")
    public String handlePlaceOrder(
            @RequestParam("receiverName") String receiverName,
            @RequestParam("receiverAddress") String receiverAddress,
            @RequestParam("receiverPhone") String receiverPhone,
            @RequestParam("paymentMethod") String paymentMethod,
            @RequestParam(value = "cartItemIds", required = false) List<Long> cartItemIds,
            @RequestParam(value = "voucherCode", required = false) String voucherCode,
            HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        String email = (String) session.getAttribute("email");
        if (email == null) {
            return "redirect:/login";
        }

        if (cartItemIds == null || cartItemIds.isEmpty()) {
            return "redirect:/cart?error=Vui lòng chọn sản phẩm để thanh toán";
        }

        User user = this.userService.getUserByEmail(email);

        try {
            this.orderService.handlePlaceOrder(user, session, receiverName, receiverAddress,
                    receiverPhone, paymentMethod, cartItemIds, voucherCode);
            return "redirect:/thanks";
        } catch (Exception e) {
            System.out.println(">>> LỖI ĐẶT HÀNG: " + e.getMessage());
            e.printStackTrace();
            String idsParam = cartItemIds.toString().replace("[", "").replace("]", "").replace(" ", "");
            try {
                String encodedMsg = java.net.URLEncoder.encode(e.getMessage(), "UTF-8");
                return "redirect:/checkout?error=" + encodedMsg + "&selectedIds=" + idsParam;
            } catch (java.io.UnsupportedEncodingException ex) {
                return "redirect:/checkout?error=Place order failed&selectedIds=" + idsParam;
            }
        }
    }

    // Trang cảm ơn sau khi đặt hàng
    @GetMapping("/thanks")
    public String getThankYouPage(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories(null));
        return "client/cart/thanks";
    }

    // Trang lịch sử đơn hàng
    @GetMapping("/order-history")
    public String getHistoryPage(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String email = (String) session.getAttribute("email");
        if (email == null)
            return "redirect:/login";

        model.addAttribute("categories", categoryService.getAllCategories(null));
        model.addAttribute("historyOrders", orderService.getCompletedOrders(email));

        return "client/order/history";
    }

    // Trang theo dõi đơn hàng đang hoạt động
    @GetMapping("/order-tracking")
    public String getTrackingPage(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String email = (String) session.getAttribute("email");
        if (email == null)
            return "redirect:/login";

        model.addAttribute("categories", categoryService.getAllCategories(null));
        model.addAttribute("activeOrders", orderService.getActiveOrders(email));

        return "client/order/tracking";
    }

    // Xử lý hủy đơn hàng
    @GetMapping("/order/cancel/{id}")
    public String handleCancelOrder(@PathVariable long id, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String email = (String) session.getAttribute("email");
        if (email == null)
            return "redirect:/login";

        Optional<Order> orderOptional = orderService.getOrderById(id);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            if (order.getStatus().equals("PENDING")) {
                order.setStatus("CANCELLED");
                orderService.handleSaveOrder(order);
            }
        }
        return "redirect:/order-tracking";
    }

    // Xem chi tiết đơn hàng
    @GetMapping("/order-detail/{id}")
    public String getOrderDetailPage(Model model, @PathVariable long id, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String email = (String) session.getAttribute("email");
        if (email == null)
            return "redirect:/login";

        User user = userService.getUserByEmail(email);
        Optional<Order> orderOptional = orderService.getOrderById(id);

        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            if (order.getUser().getId() != user.getId()) {
                return "redirect:/order-history";
            }
            model.addAttribute("order", order);
            model.addAttribute("orderDetails", orderService.getOrderDetailsByOrderId(id));
        } else {
            return "redirect:/order-history";
        }

        model.addAttribute("categories", categoryService.getAllCategories(null));
        return "client/order/detail";
    }
}