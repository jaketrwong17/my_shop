package com.example.shop.domain;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    // Tên danh mục
    @Column(nullable = false)
    private String name;

    // Mô tả danh mục
    @Column(columnDefinition = "TEXT")
    private String description;

    // Quan hệ One-to-Many: Một danh mục có nhiều sản phẩm
    // mappedBy trỏ tới biến "category" trong class Product

    // [THÊM ĐOẠN NÀY]
    // Quan hệ 1-N: Một danh mục có List các sản phẩm
    // mappedBy = "category" phải trùng tên biến "category" bên file Product.java
    @OneToMany(mappedBy = "category")
    private List<Product> products;

    // Getter và Setter cho products

    // ... các getter/setter khác

    // --- Getters and Setters ---
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}