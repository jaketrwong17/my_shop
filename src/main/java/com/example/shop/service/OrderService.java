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

    public OrderService(OrderRepository orderRepository,
            OrderDetailRepository orderDetailRepository,
            CartRepository cartRepository,
            ProductRepository productRepository,
            CartItemRepository cartItemRepository,
            VoucherRepository voucherRepository) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
        this.voucherRepository = voucherRepository;
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

    // [QUAN TRỌNG] Thay đổi hàm tính tiền để trả về MAP (Chi tiết từng món)
    // Key: ID của CartItem, Value: Số tiền được giảm
    public Map<Long, Double> calculateDiscountBreakdown(List<CartItem> items, Voucher voucher) {
        Map<Long, Double> breakdown = new HashMap<>();

        if (voucher == null)
            return breakdown;

        // 1. Voucher Toàn sàn
        if (voucher.isAll()) {
            for (CartItem item : items) {
                double itemTotal = item.getPrice() * item.getQuantity();
                double discount = itemTotal * (voucher.getDiscount() / 100.0);
                breakdown.put(item.getId(), discount);
            }
        }
        // 2. Voucher theo Danh mục
        else {
            List<Category> allowedCategories = voucher.getCategories();
            for (CartItem item : items) {
                long productCategoryId = item.getProduct().getCategory().getId();

                // Check xem sản phẩm này có nằm trong danh mục khuyến mãi không
                boolean isMatch = allowedCategories.stream()
                        .anyMatch(cat -> cat.getId() == productCategoryId);

                if (isMatch) {
                    double itemTotal = item.getPrice() * item.getQuantity();
                    double discount = itemTotal * (voucher.getDiscount() / 100.0);
                    breakdown.put(item.getId(), discount);
                } else {
                    breakdown.put(item.getId(), 0.0); // Không giảm
                }
            }
        }
        return breakdown;
    }

    @Transactional
    public void handlePlaceOrder(
            User user, HttpSession session,
            String receiverName, String receiverAddress, String receiverPhone, String paymentMethod,
            List<Long> cartItemIds, String voucherCode) throws Exception {

        // 1. Kiểm tra giỏ hàng
        Cart cart = this.cartRepository.findByUser(user);
        if (cart == null)
            throw new Exception("Giỏ hàng trống!");

        List<CartItem> cartItems = cart.getCartItems();
        List<CartItem> itemsToOrder = new ArrayList<>();

        // 2. Lọc sản phẩm cần mua & Check tồn kho
        for (CartItem item : cartItems) {
            if (cartItemIds.contains(item.getId())) {
                if (item.getProduct().getQuantity() < item.getQuantity()) {
                    throw new Exception("Sản phẩm " + item.getProduct().getName() + " không đủ số lượng.");
                }
                itemsToOrder.add(item);
            }
        }

        if (itemsToOrder.isEmpty())
            throw new Exception("Chưa chọn sản phẩm nào.");

        // 3. Tính tiền (Gốc & Giảm giá)
        double originalTotal = 0;
        double totalDiscount = 0;

        for (CartItem item : itemsToOrder) {
            originalTotal += item.getPrice() * item.getQuantity();
        }

        // Xử lý Voucher (Nếu có)
        if (voucherCode != null && !voucherCode.isEmpty()) {
            Voucher voucher = this.voucherRepository.findByCode(voucherCode);
            if (voucher != null) {
                // Gọi hàm tính chi tiết (Logic: Check danh mục -> Tính giảm cho từng món)
                Map<Long, Double> breakdown = calculateDiscountBreakdown(itemsToOrder, voucher);

                // Cộng tổng số tiền được giảm từ các món hợp lệ
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
        order.setTotalPrice(finalTotalPrice); // Lưu tổng tiền cuối cùng
        order.setPaymentStatus(paymentMethod.equals("COD") ? "UNPAID" : "PENDING");
        order.setCreatedAt(new Date());

        order = this.orderRepository.save(order);

        // 5. Lưu Chi tiết (OrderDetail) & Trừ kho & Xóa DB
        for (CartItem item : itemsToOrder) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(item.getProduct());

            // Lưu giá gốc vào đơn hàng (để sau này biết giá lúc mua là bao nhiêu)
            orderDetail.setPrice(item.getPrice());

            orderDetail.setQuantity(item.getQuantity());
            this.orderDetailRepository.save(orderDetail);

            // Trừ tồn kho
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity() - item.getQuantity());
            this.productRepository.save(product);

            // Xóa CartItem trong Database
            this.cartItemRepository.deleteById(item.getId());
        }

        // 6. [QUAN TRỌNG] FIX LỖI BADGE GIỎ HÀNG
        // Dùng removeIf để xóa chính xác các item đã mua ra khỏi danh sách trong bộ nhớ
        cart.getCartItems().removeIf(item -> cartItemIds.contains(item.getId()));

        // Cập nhật lại số lượng mới
        int newSum = cart.getCartItems().size();
        cart.setSum(newSum);
        this.cartRepository.save(cart);

        // Cập nhật Session ngay lập tức để Header hiển thị đúng
        session.setAttribute("sum", newSum);
    }
}