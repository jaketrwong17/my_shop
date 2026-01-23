package com.example.shop.domain;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "orders") // "order" là từ khóa của SQL nên phải đặt tên bảng là "orders" (số nhiều)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private double totalPrice;

    // Trạng thái đơn hàng: "PENDING" (Chờ xác nhận), "SHIPPING" (Đang giao),
    // "COMPLETED" (Hoàn thành), "CANCELLED" (Hủy)
    private String status;

    // Thông tin người nhận (Có thể khác với thông tin User đăng ký)
    private String receiverName;
    private String receiverAddress;
    private String receiverPhone;

    // Quan hệ: Một User có nhiều đơn hàng
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Quan hệ: Một đơn hàng có nhiều món (OrderDetail)
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderDetail> orderDetails;

    // --- GETTER & SETTER ---
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }

    // [THÊM TRƯỜNG NÀY VÀO]
    private String paymentRef; // Mã giao dịch thanh toán (nếu có)

    // [THÊM GETTER & SETTER]
    public String getPaymentRef() {
        return paymentRef;
    }

    public void setPaymentRef(String paymentRef) {
        this.paymentRef = paymentRef;
    }
}