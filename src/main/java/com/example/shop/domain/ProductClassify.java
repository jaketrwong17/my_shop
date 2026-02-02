package com.example.shop.domain;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "product_colors")
public class ProductClassify implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String colorName;
    private long quantity;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public ProductClassify() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getColorName() {
        return colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

}