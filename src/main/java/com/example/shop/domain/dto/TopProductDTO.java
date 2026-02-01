package com.example.shop.domain.dto;

public class TopProductDTO {
    private String productName;
    private String productImage;
    private Long quantitySold;
    private Double totalRevenue;

    public TopProductDTO(String productName, String productImage, Long quantitySold, Double totalRevenue) {
        this.productName = productName;
        this.productImage = productImage;
        this.quantitySold = quantitySold;
        this.totalRevenue = totalRevenue;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Long getQuantitySold() {
        return quantitySold;
    }

    public void setQuantitySold(Long quantitySold) {
        this.quantitySold = quantitySold;
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}