package com.example.shop.service;

import com.example.shop.domain.Product;
import com.example.shop.repository.ProductRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Lấy danh sách sản phẩm có lọc theo từ khóa và danh mục
    public List<Product> getAllProducts(String keyword, Long categoryId) {
        if (keyword != null && !keyword.isEmpty() && categoryId != null) {
            return productRepository.findByNameContainingIgnoreCaseAndCategoryId(keyword, categoryId);
        }
        if (keyword != null && !keyword.isEmpty()) {
            return productRepository.findByNameContainingIgnoreCase(keyword);
        }
        if (categoryId != null) {
            return productRepository.findByCategoryId(categoryId);
        }
        return productRepository.findAll();
    }

    // Lưu hoặc Cập nhật sản phẩm
    public Product handleSaveProduct(Product product) {
        return productRepository.save(product);
    }

    // Tìm kiếm theo ID (trả về Optional để tránh NullPointerException)
    public Optional<Product> fetchProductById(long id) {
        return productRepository.findById(id);
    }

    // Xóa sản phẩm
    public void deleteProduct(long id) {
        productRepository.deleteById(id);
    }

    public List<Product> fetchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Product> fetchProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }
}