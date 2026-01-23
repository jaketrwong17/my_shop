package com.example.shop.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "products")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    // Tên sản phẩm
    @Column(nullable = false)
    private String name;

    // Giá bán
    private double price;

    // --- [QUAN TRỌNG: ĐỔI TỪ 1 ẢNH SANG DANH SÁCH ẢNH] ---
    // Quan hệ Một - Nhiều: Một sản phẩm có nhiều ảnh
    // mappedBy = "product": Phải trùng với tên biến "product" bên file
    // ProductImage.java
    // cascade = CascadeType.ALL: Khi xóa Product, tự động xóa hết ảnh của nó
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> images = new ArrayList<>();
    // Mô tả chi tiết (TEXT để lưu bài viết dài)
    @Column(columnDefinition = "TEXT")
    private String detailDesc;

    // Mô tả ngắn
    private String shortDesc;

    // Số lượng tồn kho
    private long quantity;

    // Số lượng đã bán
    private long sold;

    // Hãng sản xuất
    private String factory;

    // Nhóm khách hàng mục tiêu
    private String target;

    // Quan hệ Nhiều - Một: Sản phẩm thuộc về 1 danh mục
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    // --- Constructor ---
    public Product() {
    }

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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    // [GETTER/SETTER CHO LIST ẢNH]
    public List<ProductImage> getImages() {
        return images;
    }

    public void setImages(List<ProductImage> images) {
        this.images = images;
    }

    public String getDetailDesc() {
        return detailDesc;
    }

    public void setDetailDesc(String detailDesc) {
        this.detailDesc = detailDesc;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public void setShortDesc(String shortDesc) {
        this.shortDesc = shortDesc;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public long getSold() {
        return sold;
    }

    public void setSold(long sold) {
        this.sold = sold;
    }

    public String getFactory() {
        return factory;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Product [id=" + id + ", name=" + name + ", price=" + price + "]";
    }
}