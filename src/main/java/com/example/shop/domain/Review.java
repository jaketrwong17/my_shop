package com.example.shop.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "reviews")
public class Review implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    // Số sao đánh giá (1-5)
    private int rating;

    // Nội dung bình luận
    @Column(columnDefinition = "TEXT")
    private String content;

    // Ngày đánh giá
    private Date createdAt;

    // Trạng thái kiểm duyệt (VD: "PENDING", "APPROVED", "HIDDEN")
    private String status;

    // Người đánh giá
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Sản phẩm được đánh giá
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    // --- Constructor ---
    public Review() {
    }

    @PrePersist
    public void onCreate() {
        this.createdAt = new Date();
        if (this.status == null) {
            this.status = "PENDING"; // Mặc định chờ duyệt nếu cần
        }
    }

    // --- Getters and Setters ---
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}