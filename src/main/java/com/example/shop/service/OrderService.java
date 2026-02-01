package com.example.shop.service;

import com.example.shop.domain.*;
import com.example.shop.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final VoucherRepository voucherRepository;
    private final ProductColorRepository productColorRepository; // THÊM REPO NÀY
    private UserRepository userRepository;

    public OrderService(OrderRepository orderRepository,
            OrderDetailRepository orderDetailRepository,
            CartRepository cartRepository,
            ProductRepository productRepository,
            CartItemRepository cartItemRepository,
            VoucherRepository voucherRepository,
            ProductColorRepository productColorRepository,
            UserRepository userRepository) { // INJECT REPO
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
        this.voucherRepository = voucherRepository;
        this.productColorRepository = productColorRepository;
        this.userRepository = userRepository;
    }

    // ==================== ADMIN ====================
    public List<Order> getAllOrders() {
        return this.orderRepository.findAll();
    }

    public Optional<Order> getOrderById(long id) {
        return this.orderRepository.findById(id);
    }

    public void handleSaveOrder(Order order) {
        this.orderRepository.save(order);
    }

    public void deleteOrderById(long id) {
        this.orderRepository.deleteById(id);
    }

    // ==================== CLIENT ====================

    public Voucher getVoucherByCode(String code) {
        return this.voucherRepository.findByCode(code);
    }

    public Map<Long, Double> calculateDiscountBreakdown(List<CartItem> items, Voucher voucher) {
        Map<Long, Double> breakdown = new HashMap<>();
        if (voucher == null)
            return breakdown;

        if (voucher.isAll()) {
            for (CartItem item : items) {
                double itemTotal = item.getPrice() * item.getQuantity();
                double discount = itemTotal * (voucher.getDiscount() / 100.0);
                breakdown.put(item.getId(), discount);
            }
        } else {
            List<Category> allowedCategories = voucher.getCategories();
            for (CartItem item : items) {
                long productCategoryId = item.getProduct().getCategory().getId();
                boolean isMatch = allowedCategories.stream()
                        .anyMatch(cat -> cat.getId() == productCategoryId);
                if (isMatch) {
                    double itemTotal = item.getPrice() * item.getQuantity();
                    double discount = itemTotal * (voucher.getDiscount() / 100.0);
                    breakdown.put(item.getId(), discount);
                } else {
                    breakdown.put(item.getId(), 0.0);
                }
            }
        }
        return breakdown;
    }

    @Transactional(rollbackFor = Exception.class)
    public void handlePlaceOrder(
            User user, HttpSession session,
            String receiverName, String receiverAddress, String receiverPhone, String paymentMethod,
            List<Long> cartItemIds, String voucherCode) throws Exception {

        // 1. Kiểm tra giỏ hàng
        Cart cart = this.cartRepository.findByUser(user);
        if (cart == null || cart.getCartItems().isEmpty())
            throw new Exception("Giỏ hàng trống!");

        List<CartItem> cartItems = cart.getCartItems();
        List<CartItem> itemsToOrder = new ArrayList<>();

        // 2. Lọc sản phẩm & Kiểm tra kho THEO MÀU SẮC
        for (CartItem item : cartItems) {
            if (cartItemIds.contains(item.getId())) {
                ProductColor pc = item.getProductColor();

                if (pc == null) {
                    throw new Exception("Sản phẩm " + item.getProduct().getName() + " chưa chọn màu sắc!");
                }

                if (pc.getQuantity() < item.getQuantity()) {
                    throw new Exception("Sản phẩm " + item.getProduct().getName() + " màu "
                            + pc.getColorName() + " không đủ số lượng (Chỉ còn " + pc.getQuantity() + ").");
                }
                itemsToOrder.add(item);
            }
        }

        if (itemsToOrder.isEmpty())
            throw new Exception("Chưa chọn sản phẩm nào để thanh toán.");

        // 3. Tính tiền
        double originalTotal = 0;
        double totalDiscount = 0;
        for (CartItem item : itemsToOrder) {
            originalTotal += item.getPrice() * item.getQuantity();
        }

        if (voucherCode != null && !voucherCode.isEmpty()) {
            Voucher voucher = this.voucherRepository.findByCode(voucherCode);
            if (voucher != null) {
                Map<Long, Double> breakdown = calculateDiscountBreakdown(itemsToOrder, voucher);
                for (Double d : breakdown.values()) {
                    totalDiscount += d;
                }
            }
        }

        double finalTotalPrice = originalTotal - totalDiscount;
        if (finalTotalPrice < 0)
            finalTotalPrice = 0;

        // 4. Lưu Đơn hàng (Order)
        Order order = new Order();
        order.setUser(user);
        order.setReceiverName(receiverName);
        order.setReceiverAddress(receiverAddress);
        order.setReceiverPhone(receiverPhone);
        order.setPaymentMethod(paymentMethod);
        order.setStatus("PENDING");
        order.setTotalPrice(finalTotalPrice);
        order.setPaymentStatus(paymentMethod.equals("COD") ? "UNPAID" : "PENDING");
        order.setCreatedAt(new Date());

        order = this.orderRepository.save(order);

        // 5. Lưu Chi tiết (OrderDetail) & TRỪ KHO & LƯU MÀU
        for (CartItem item : itemsToOrder) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(item.getProduct());
            orderDetail.setPrice(item.getPrice());
            orderDetail.setQuantity(item.getQuantity());

            // --- PHẦN QUAN TRỌNG ĐÃ SỬA ---
            if (item.getProductColor() != null) {
                // 1. Lưu tên màu vào chi tiết đơn hàng (để hiển thị ở lịch sử)
                orderDetail.setSelectedColor(item.getProductColor().getColorName());

                // 2. Trừ kho
                ProductColor pc = item.getProductColor();
                pc.setQuantity(pc.getQuantity() - item.getQuantity());
                this.productColorRepository.save(pc);
            }
            // -----------------------------

            this.orderDetailRepository.save(orderDetail);

            // Xóa CartItem trong Database
            this.cartItemRepository.deleteById(item.getId());
        }

        // 6. Cập nhật Badge giỏ hàng và Session
        cart.getCartItems().removeIf(item -> cartItemIds.contains(item.getId()));
        int newSum = cart.getCartItems().size();
        cart.setSum(newSum);
        this.cartRepository.save(cart);
        session.setAttribute("sum", newSum);
    }
    // Trong OrderService.java

    public List<Order> getAllOrders(String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return orderRepository.searchOrders(keyword);
        }
        return orderRepository.findAll();
    }

    public List<Order> getCompletedOrders(String email) {
        User user = userRepository.findByEmail(email);
        List<String> statuses = List.of("COMPLETED", "CANCELLED");
        return orderRepository.findByUserAndStatusIn(user, statuses);
    }

    // Hàm lấy đơn hàng cho trang Theo dõi (Chờ duyệt hoặc Đang giao)
    public List<Order> getActiveOrders(String email) {
        User user = userRepository.findByEmail(email);
        // THÊM TRẠNG THÁI "CONFIRMED" VÀO LIST NÀY
        List<String> statuses = List.of("PENDING", "CONFIRMED", "SHIPPING");
        return orderRepository.findByUserAndStatusIn(user, statuses);
    }
    // ... Các hàm cũ giữ nguyên ...

    // --- THÊM HÀM NÀY ĐỂ FIX LỖI BLANK CHI TIẾT ĐƠN HÀNG ---
    @Transactional
    public List<OrderDetail> getOrderDetailsByOrderId(long id) {
        Optional<Order> orderOptional = this.orderRepository.findById(id);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            // Gọi hàm này để Hibernate ép buộc tải dữ liệu chi tiết (Initialize)
            // Khắc phục lỗi Lazy Loading khiến list bị rỗng bên Controller
            order.getOrderDetails().size();
            return order.getOrderDetails();
        }
        return new ArrayList<>();
    }

    public boolean hasUserBoughtProduct(String email, long productId) {
        return orderDetailRepository.existsByOrderUserEmailAndProductIdAndOrderStatus(email, productId, "COMPLETED");
    }
}
