package com.example.shop.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "users") // Đặt tên bảng là 'users' để tránh trùng từ khóa SQL
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    // Email dùng để đăng nhập, không được trùng
    @Column(nullable = false, unique = true)
    private String email;

    private String password; // Lưu mật khẩu đã mã hóa

    @Column(name = "full_name")
    private String fullName;

    private String phone;

    private String address;

    // Trạng thái tài khoản (1 = Active).
    // Giữ lại để khớp với SQL Script của bạn, dù logic chính đang dùng isLocked.
    private long status = 1;

    // [QUAN TRỌNG] Biến này dùng cho chức năng Khóa/Mở khóa của Admin
    // false = Hoạt động bình thường, true = Bị khóa
    private boolean isLocked = false;

    // --- CÁC MỐI QUAN HỆ (RELATIONSHIPS) ---

    // 1. User - Role (N - 1): Một User thuộc về một Role
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    // 2. User - Cart (1 - 1): User có một Giỏ hàng
    // CascadeType.ALL: Xóa User thì xóa luôn Giỏ hàng
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Cart cart;

    // 3. User - Order (1 - N): User có danh sách Đơn hàng
    // [MỚI] Cần cái này để kiểm tra xem User có đơn hàng nào không trước khi xóa
    // mappedBy = "user": Bên Order là bên giữ khóa ngoại
    @OneToMany(mappedBy = "user")
    private List<Order> orders;

    // --- CONSTRUCTORS ---

    public User() {
    }

    // --- GETTER & SETTER ---

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }

    public boolean getIsLocked() { // Getter chuẩn cho boolean trong JSP
        return isLocked;
    }

    public void setIsLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    // --- TO STRING (Tránh in Cart/Orders/Role để không bị vòng lặp vô tận) ---
    @Override
    public String toString() {
        return "User [id=" + id + ", email=" + email + ", fullName=" + fullName + ", address=" + address + "]";
    }
}