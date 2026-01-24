package com.example.shop.controller.client;

import com.example.shop.domain.Product;
import com.example.shop.service.CategoryService;
import com.example.shop.service.ProductService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

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
            HttpServletRequest request,
            @RequestParam(required = false) String search, // Thêm required = false để không bắt buộc có từ khóa
            @RequestParam(required = false) Long categoryId) {

        // Khởi tạo sum cho badge giỏ hàng
        HttpSession session = request.getSession(true);
        if (session.getAttribute("sum") == null) {
            session.setAttribute("sum", 0);
        }

        List<Product> products;

        // Ưu tiên lọc theo tìm kiếm, nếu không có thì lọc theo danh mục
        if (search != null && !search.isEmpty()) {
            products = productService.fetchProductsByName(search);
        } else if (categoryId != null) {
            products = productService.fetchProductsByCategory(categoryId);
        } else {
            // Mặc định lấy tất cả sản phẩm
            products = productService.getAllProducts(null, null);
        }

        model.addAttribute("products", products);
        model.addAttribute("categories", categoryService.getAllCategories(null));

        return "client/homepage/show";
    }

}