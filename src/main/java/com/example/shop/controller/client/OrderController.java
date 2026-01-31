package com.example.shop.controller.client;

import com.example.shop.domain.Cart;
import com.example.shop.domain.CartItem;
import com.example.shop.domain.User;
import com.example.shop.domain.Voucher;
import com.example.shop.service.CategoryService; // <--- 1. IMPORT SERVICE
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
import java.util.Optional; // Để sửa lỗi Optional
import com.example.shop.domain.Order; // Để sửa lỗi Order

@Controller("clientOrderController")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;
    private final ProductService productService;
    private final CategoryService categoryService; // <--- 2. KHAI BÁO BIẾN

    // <--- 3. INJECT CATEGORY SERVICE VÀO CONSTRUCTOR
    public OrderController(OrderService orderService, UserService userService,
            ProductService productService, CategoryService categoryService) {
        this.orderService = orderService;
        this.userService = userService;
        this.productService = productService;
        this.categoryService = categoryService;
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

        // <--- 4. THÊM DÒNG NÀY ĐỂ HIỆN DANH MỤC TRÊN HEADER TRANG CHECKOUT
        model.addAttribute("categories", categoryService.getAllCategories(null));

        return "client/cart/checkout";
    }

    @PostMapping("/place-order")
    public String handlePlaceOrder(
            @RequestParam("receiverName") String receiverName,
            @RequestParam("receiverAddress") String receiverAddress,
            @RequestParam("receiverPhone") String receiverPhone,
            @RequestParam("paymentMethod") String paymentMethod,
            @RequestParam(value = "cartItemIds", required = false) List<Long> cartItemIds,
            @RequestParam(value = "voucherCode", required = false) String voucherCode,
            HttpServletRequest request) {

        // 1. Kiểm tra session đăng nhập
        HttpSession session = request.getSession(false);
        String email = (String) session.getAttribute("email");
        if (email == null) {
            return "redirect:/login";
        }

        // 2. Kiểm tra dữ liệu đầu vào
        if (cartItemIds == null || cartItemIds.isEmpty()) {
            return "redirect:/cart?error=Vui lòng chọn sản phẩm để thanh toán";
        }

        User user = this.userService.getUserByEmail(email);

        try {
            // 3. Gọi Service xử lý đặt hàng
            this.orderService.handlePlaceOrder(user, session, receiverName, receiverAddress,
                    receiverPhone, paymentMethod, cartItemIds, voucherCode);

            // 4. THÀNH CÔNG
            return "redirect:/thanks";

        } catch (Exception e) {
            // 5. THẤT BẠI: Xử lý lỗi
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

    @GetMapping("/thanks")
    public String getThankYouPage(Model model) { // <--- Nhớ thêm tham số Model vào đây

        // <--- 5. THÊM DÒNG NÀY ĐỂ HIỆN DANH MỤC TRÊN HEADER TRANG THANKS
        model.addAttribute("categories", categoryService.getAllCategories(null));

        return "client/cart/thanks";
    }
    // Route cho Lịch sử đơn hàng
    // ... (Các phần import và code bên trên giữ nguyên) ...

    // Route cho Lịch sử đơn hàng
    // Route cho Lịch sử đơn hàng
    // --- CẬP NHẬT: LỊCH SỬ ĐƠN HÀNG ---
    @GetMapping("/order-history")
    public String getHistoryPage(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String email = (String) session.getAttribute("email");
        if (email == null)
            return "redirect:/login";

        model.addAttribute("categories", categoryService.getAllCategories(null));
        // Lấy danh sách đơn đã hoàn thành/đã hủy
        model.addAttribute("historyOrders", orderService.getCompletedOrders(email));

        return "client/order/history";
    }

    // --- CẬP NHẬT: THEO DÕI ĐƠN HÀNG ---
    @GetMapping("/order-tracking")
    public String getTrackingPage(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String email = (String) session.getAttribute("email");
        if (email == null)
            return "redirect:/login";

        model.addAttribute("categories", categoryService.getAllCategories(null));
        // Sử dụng biến "activeOrders" để đồng bộ với JSP
        model.addAttribute("activeOrders", orderService.getActiveOrders(email));

        return "client/order/tracking";
    }

    // --- MỚI: HỦY ĐƠN HÀNG (Chỉ cho phép khi đang PENDING) ---
    @GetMapping("/order/cancel/{id}")
    public String handleCancelOrder(@PathVariable long id, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String email = (String) session.getAttribute("email");
        if (email == null)
            return "redirect:/login";

        Optional<Order> orderOptional = orderService.getOrderById(id);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            // Kiểm tra bảo mật: User hiện tại có phải chủ đơn hàng không
            // Kiểm tra logic: Chỉ hủy được đơn đang Chờ xử lý (PENDING)
            if (order.getStatus().equals("PENDING")) {
                order.setStatus("CANCELLED");
                orderService.handleSaveOrder(order);
            }
        }
        return "redirect:/order-tracking";
    }

    // --- MỚI: XEM CHI TIẾT ĐƠN HÀNG & ĐÁNH GIÁ ---
    // ... Các code khác giữ nguyên ...

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
            // Bảo mật: Chỉ xem được đơn của chính mình
            if (order.getUser().getId() != user.getId()) {
                return "redirect:/order-history";
            }

            model.addAttribute("order", order);

            // --- SỬA ĐOẠN NÀY ---
            // Thay vì dùng order.getOrderDetails(), hãy gọi hàm mới từ Service
            // Để đảm bảo danh sách sản phẩm được tải đầy đủ
            model.addAttribute("orderDetails", orderService.getOrderDetailsByOrderId(id));
            // --------------------

        } else {
            return "redirect:/order-history";
        }

        model.addAttribute("categories", categoryService.getAllCategories(null));
        return "client/order/detail";
    }
}
