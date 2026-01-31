package com.example.shop.repository;

import com.example.shop.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Giữ lại các hàm cũ của bạn
    List<Product> findByNameContainingIgnoreCaseAndCategoryId(String name, Long categoryId);

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByCategoryId(Long categoryId);

    // --- THÊM HÀM MỚI NÀY ---
    // Logic: Nếu quantity > 0 thì xếp nhóm 0 (trên), ngược lại xếp nhóm 1 (dưới).
    // Sau đó xếp theo ID giảm dần (sản phẩm mới nhất lên đầu trong cùng nhóm)
    @Query("SELECT p FROM Product p ORDER BY CASE WHEN p.quantity > 0 THEN 0 ELSE 1 END ASC, p.id DESC")
    Page<Product> findAllSortedByStock(Pageable pageable);
}