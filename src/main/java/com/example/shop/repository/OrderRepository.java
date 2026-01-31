package com.example.shop.repository;

import com.example.shop.domain.Order;
import com.example.shop.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Lấy danh sách đơn hàng của một User cụ thể (Lịch sử mua hàng)
    List<Order> findByUser(User user);

    // (Admin) Tìm đơn hàng theo mã giao dịch VNPay (để đối soát)
    // Order findByPaymentRef(String paymentRef);
    @Query("SELECT o FROM Order o WHERE " +
            "o.receiverName LIKE %?1% OR " +
            "o.receiverPhone LIKE %?1% OR " +
            "CAST(o.id as string) LIKE %?1%")
    List<Order> searchOrders(String keyword);
}