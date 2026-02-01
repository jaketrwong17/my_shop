package com.example.shop.service;

import com.example.shop.domain.*;
import com.example.shop.repository.*;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
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
    private final ProductColorRepository productColorRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository,
            OrderDetailRepository orderDetailRepository,
            CartRepository cartRepository,
            ProductRepository productRepository,
            CartItemRepository cartItemRepository,
            VoucherRepository voucherRepository,
            ProductColorRepository productColorRepository,
            UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
        this.voucherRepository = voucherRepository;
        this.productColorRepository = productColorRepository;
        this.userRepository = userRepository;
    }

    // Lấy danh sách tất cả đơn hàng cho Admin
    public List<Order> getAllOrders() {
        return this.orderRepository.findAll();
    }

    // Tìm kiếm đơn hàng theo ID
    public Optional<Order> getOrderById(long id) {
        return this.orderRepository.findById(id);
    }

    // Lưu thông tin đơn hàng vào cơ sở dữ liệu
    public void handleSaveOrder(Order order) {
        this.orderRepository.save(order);
    }

    // Xóa đơn hàng theo ID
    public void deleteOrderById(long id) {
        this.orderRepository.deleteById(id);
    }

    // Tìm kiếm Voucher theo mã code
    public Voucher getVoucherByCode(String code) {
        return this.voucherRepository.findByCode(code);
    }

    // Tính toán số tiền được giảm cho từng sản phẩm dựa trên Voucher
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

    // Xử lý quy trình đặt hàng: kiểm tra kho, tính tiền, trừ tồn kho và lưu đơn
    // hàng
    @Transactional(rollbackFor = Exception.class)
    public void handlePlaceOrder(
            User user, HttpSession session,
            String receiverName, String receiverAddress, String receiverPhone, String paymentMethod,
            List<Long> cartItemIds, String voucherCode) throws Exception {

        Cart cart = this.cartRepository.findByUser(user);
        if (cart == null || cart.getCartItems().isEmpty())
            throw new Exception("Giỏ hàng trống!");

        List<CartItem> cartItems = cart.getCartItems();
        List<CartItem> itemsToOrder = new ArrayList<>();

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

        for (CartItem item : itemsToOrder) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(item.getProduct());
            orderDetail.setPrice(item.getPrice());
            orderDetail.setQuantity(item.getQuantity());

            if (item.getProductColor() != null) {
                orderDetail.setSelectedColor(item.getProductColor().getColorName());
                ProductColor pc = item.getProductColor();
                pc.setQuantity(pc.getQuantity() - item.getQuantity());
                this.productColorRepository.save(pc);
            }

            this.orderDetailRepository.save(orderDetail);
            this.cartItemRepository.deleteById(item.getId());
        }

        cart.getCartItems().removeIf(item -> cartItemIds.contains(item.getId()));
        int newSum = cart.getCartItems().size();
        cart.setSum(newSum);
        this.cartRepository.save(cart);
        session.setAttribute("sum", newSum);
    }

    // Tìm kiếm đơn hàng theo từ khóa cho Admin
    public List<Order> getAllOrders(String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return orderRepository.searchOrders(keyword);
        }
        return orderRepository.findAll();
    }

    // Lấy danh sách đơn hàng đã hoàn thành hoặc đã hủy của người dùng
    public List<Order> getCompletedOrders(String email) {
        User user = userRepository.findByEmail(email);
        List<String> statuses = List.of("COMPLETED", "CANCELLED");
        return orderRepository.findByUserAndStatusIn(user, statuses);
    }

    // Lấy danh sách đơn hàng đang trong quá trình xử lý của người dùng
    public List<Order> getActiveOrders(String email) {
        User user = userRepository.findByEmail(email);
        List<String> statuses = List.of("PENDING", "CONFIRMED", "SHIPPING");
        return orderRepository.findByUserAndStatusIn(user, statuses);
    }

    // Lấy danh sách chi tiết đơn hàng và xử lý tải dữ liệu Lazy Loading
    @Transactional
    public List<OrderDetail> getOrderDetailsByOrderId(long id) {
        Optional<Order> orderOptional = this.orderRepository.findById(id);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            order.getOrderDetails().size();
            return order.getOrderDetails();
        }
        return new ArrayList<>();
    }

    // Kiểm tra xem người dùng đã từng mua thành công sản phẩm này chưa
    public boolean hasUserBoughtProduct(String email, long productId) {
        return orderDetailRepository.existsByOrderUserEmailAndProductIdAndOrderStatus(email, productId, "COMPLETED");
    }

    // Tính tổng doanh thu từ các đơn hàng hoàn thành
    public Double calculateTotalRevenue() {
        return orderRepository.calculateTotalRevenue();
    }

    // Đếm tổng số lượng đơn hàng đã hoàn thành
    public long countAllOrders() {
        return orderRepository.countByStatus("COMPLETED");
    }

}