package com.example.shop.domain.dto;

public class TopProductDTO {
    private Long productId; // Thêm trường này
    private String productName;
    private String productImage;
    private Long quantitySold;
    private Double totalRevenue;

    // QUAN TRỌNG: Constructor phải nhận đúng 5 tham số theo thứ tự này
    public TopProductDTO(Long productId, String productName, String productImage, Long quantitySold,
            Double totalRevenue) {
        this.productId = productId;
        this.productName = productName;
        this.productImage = productImage;
        this.quantitySold = quantitySold;
        this.totalRevenue = totalRevenue;
    }

    // Đừng quên thêm Getter cho productId để JSP đọc được
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    // Các Getter/Setter còn lại giữ nguyên...
    public String getProductName() {
        return productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public Long getQuantitySold() {
        return quantitySold;
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }
}