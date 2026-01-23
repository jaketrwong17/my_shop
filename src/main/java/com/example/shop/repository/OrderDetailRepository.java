package com.example.shop.repository;

import com.example.shop.domain.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    // Hiện tại chỉ cần các hàm CRUD cơ bản có sẵn của JpaRepository
    // như save(), saveAll(), delete() là đủ.
}