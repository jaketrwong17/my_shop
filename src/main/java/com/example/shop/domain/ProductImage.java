package com.example.shop.domain;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "product_images")
public class ProductImage implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String imageUrl; // Tên file ảnh (ví dụ: abc-xyz.jpg)

    // Quan hệ Nhiều - Một: Nhiều ảnh thuộc về một sản phẩm
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id") // Tên cột khóa ngoại trong bảng product_images
    private Product product;

    // --- Constructors, Getters, Setters ---
    public ProductImage() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}