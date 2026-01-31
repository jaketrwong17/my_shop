package com.example.shop.repository;

import com.example.shop.domain.ProductColor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductColorRepository extends JpaRepository<ProductColor, Long> {
    // Nếu sau này cần tìm màu theo tên hoặc sản phẩm, bạn có thể thêm method ở đây
}