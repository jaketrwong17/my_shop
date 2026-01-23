package com.example.shop.controller.client;

import com.example.shop.domain.Product;
import com.example.shop.service.CategoryService;
import com.example.shop.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@Controller
public class HomePageController {

    private final ProductService productService;
    private final CategoryService categoryService;

    public HomePageController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping("/")
    public String getHomePage(Model model,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId) {

        List<Product> products;

        if (search != null && !search.isEmpty()) {
            products = productService.fetchProductsByName(search);
        } else if (categoryId != null) {
            products = productService.fetchProductsByCategory(categoryId);
        } else {
            products = productService.getAllProducts(null, null); // Lấy tất cả
        }

        model.addAttribute("products", products);
        model.addAttribute("categories", categoryService.getAllCategories(null));
        return "client/homepage/show";
    }
}