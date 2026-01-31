package com.example.shop.repository;

import com.example.shop.domain.Cart;
import com.example.shop.domain.CartItem;
import com.example.shop.domain.Product;
import com.example.shop.domain.ProductColor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    CartItem findByCartAndProductAndProductColor(Cart cart, Product product, ProductColor productColor);
}