package com.example.shop.controller.client;

import com.example.shop.domain.Cart;
import com.example.shop.domain.CartItem;
import com.example.shop.domain.Product;
import com.example.shop.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List; // BỔ SUNG IMPORT LIST

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ItemController {

    private final ProductService productService;

    public ItemController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/product/{id}")
    public String getProductDetail(Model model, @PathVariable long id) {
        Product product = productService.fetchProductById(id).get();
        model.addAttribute("product", product);
        return "client/product/detail";
    }

    @GetMapping("/cart")
    public String getCartPage(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String email = (String) session.getAttribute("email");

        // Lấy thông tin giỏ hàng của User từ database
        Cart cart = this.productService.fetchCartByUserEmail(email);

        // LƯU Ý: Sửa getCartItems thành tên List bạn đặt trong class Cart
        List<CartItem> cartItems = (cart != null) ? cart.getCartItems() : new ArrayList<>();

        double totalPrice = 0;
        for (CartItem item : cartItems) {
            // SỬA LỖI: Gọi qua hàm Getter getQuantity() và getPrice()
            totalPrice += item.getPrice() * item.getQuantity();
        }

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);

        return "client/cart/show";
    }

    // Sửa đường dẫn thành /add-to-cart để khớp với yêu cầu POST từ trình duyệt
    @PostMapping("/add-to-cart")
    public String addProductToCart(
            HttpServletRequest request,
            @RequestParam("productId") long id, // Lấy productId từ input hidden của form
            @RequestParam("quantity") long quantity) {

        HttpSession session = request.getSession(false);
        String email = (String) session.getAttribute("email");

        // Gọi service để xử lý thêm sản phẩm vào database
        this.productService.handleAddProductToCart(email, id, session, quantity);

        // Sau khi thêm thành công, chuyển hướng người dùng về trang giỏ hàng
        return "redirect:/cart";
    }

    @PostMapping("/add-product-to-cart/{id}")
    public String addProductToCart(@PathVariable long id, HttpServletRequest request,
            @RequestParam("quantity") long quantity) {
        HttpSession session = request.getSession(false);
        String email = (String) session.getAttribute("email");

        // 1. Lưu sản phẩm vào Database
        this.productService.handleAddProductToCart(email, id, session, quantity);

        // 2. Lấy địa chỉ trang hiện tại khách đang đứng
        String referer = request.getHeader("Referer");

        // 3. QUAN TRỌNG: Quay lại đúng trang cũ
        // Nó sẽ load lại trang này, cập nhật số lượng trên Header mà không nhảy trang
        // mới
        return "redirect:" + referer;
    }
}