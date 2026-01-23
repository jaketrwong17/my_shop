package com.example.shop.repository;

import com.example.shop.domain.Order;
import com.example.shop.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Lấy danh sách đơn hàng của một User cụ thể (Lịch sử mua hàng)
    List<Order> findByUser(User user);

    // (Admin) Tìm đơn hàng theo mã giao dịch VNPay (để đối soát)
    // Order findByPaymentRef(String paymentRef);
}