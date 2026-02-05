package com.example.shop.repository;

import com.example.shop.domain.Order;
import com.example.shop.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // SỬA DÒNG NÀY: Thêm OrderByIdDesc vào đuôi
    List<Order> findByUserOrderByIdDesc(User user);

    @Query("SELECT o FROM Order o WHERE " +
            "o.receiverName LIKE %?1% OR " +
            "o.receiverPhone LIKE %?1% OR " +
            "CAST(o.id as string) LIKE %?1%")
    List<Order> searchOrders(String keyword);

    // Dòng này bạn đã có sẵn, rất tốt (Dùng cho lịch sử đơn hàng theo trạng thái)
    List<Order> findByUserAndStatusInOrderByIdDesc(User user, List<String> status);

    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.status = 'COMPLETED'")
    Double calculateTotalRevenue();

    long countByStatus(String status);

    long count();
}