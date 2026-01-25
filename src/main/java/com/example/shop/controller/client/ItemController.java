package com.example.shop.controller.client;

import com.example.shop.domain.Cart;
import com.example.shop.domain.CartItem;
import com.example.shop.domain.Product;
import com.example.shop.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

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

    // ================== [ĐÃ SỬA] ==================
    @PostMapping("/add-product-to-cart/{id}")
    public String addProductToCart(@PathVariable long id, HttpServletRequest request,
            @RequestParam("quantity") long quantity) {

        HttpSession session = request.getSession(true);
        String email = (String) session.getAttribute("email");

        // 1. Xử lý thêm vào Database (logic cũ của bạn)
        this.productService.handleAddProductToCart(email, id, session, quantity);

        // 2. [QUAN TRỌNG] Cập nhật lại số lượng (sum) vào Session ngay lập tức
        int newSum = 0;
        if (email != null) {
            // Nếu User đã đăng nhập: Lấy giỏ hàng từ DB để đếm
            Cart cart = this.productService.fetchCartByUserEmail(email);
            if (cart != null) {
                newSum = cart.getCartItems().size();
            }
        } else {
            // Nếu là khách (Guest): Lấy từ session guestCart để đếm
            List<CartItem> guestCart = (List<CartItem>) session.getAttribute("guestCart");
            if (guestCart != null) {
                newSum = guestCart.size();
            }
        }

        // Lưu biến 'sum' để header hiển thị số mới
        session.setAttribute("sum", newSum);

        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }
    // ==============================================

    @GetMapping("/cart")
    public String getCartPage(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        String email = (String) session.getAttribute("email");
        List<CartItem> cartItems;

        if (email == null) {
            cartItems = (List<CartItem>) session.getAttribute("guestCart");
            if (cartItems == null)
                cartItems = new ArrayList<>();
        } else {
            Cart cart = this.productService.fetchCartByUserEmail(email);
            cartItems = (cart != null) ? cart.getCartItems() : new ArrayList<>();
        }

        double totalPrice = 0;
        for (CartItem item : cartItems) {
            totalPrice += item.getPrice() * item.getQuantity();
        }

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);
        return "client/cart/show";
    }

    @PostMapping("/update-cart-quantity")
    public String updateCartQuantity(@RequestParam("cartItemId") long cartItemId,
            @RequestParam("action") String action,
            HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        this.productService.handleUpdateCartQuantity(cartItemId, action, session);
        return "redirect:/cart";
    }

    @GetMapping("/delete-cart-item/{id}")
    public String deleteCartItem(@PathVariable long id, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        this.productService.handleDeleteCartItem(id, session);
        return "redirect:/cart";
    }

    @GetMapping("/delete-multiple-cart-items")
    public String deleteMultipleCartItems(@RequestParam("ids") List<Long> ids, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (ids != null && !ids.isEmpty()) {
            for (Long id : ids) {
                this.productService.handleDeleteCartItem(id, session);
            }
        }
        return "redirect:/cart";
    }
}