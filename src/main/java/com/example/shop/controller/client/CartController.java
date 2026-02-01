package com.example.shop.controller.client;

import com.example.shop.domain.Cart;
import com.example.shop.domain.CartItem;
import com.example.shop.service.CategoryService;
import com.example.shop.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class CartController {

    private final ProductService productService;
    private final CategoryService categoryService;

    public CartController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    // Hiển thị trang giỏ hàng cho người dùng đã đăng nhập hoặc khách (guest)
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
        model.addAttribute("categories", categoryService.getAllCategories(null));

        return "client/cart/show";
    }

    // Thêm sản phẩm vào giỏ hàng và quay trở lại trang trước đó
    @PostMapping("/add-product-to-cart/{id}")
    public String addProductToCart(@PathVariable long id,
            @RequestParam("quantity") long quantity,
            @RequestParam("colorId") long colorId,
            HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        String email = (String) session.getAttribute("email");
        this.productService.handleAddProductToCart(email, id, colorId, session, quantity);
        return "redirect:" + request.getHeader("Referer");
    }

    // Cập nhật số lượng của một sản phẩm trong giỏ hàng (tăng/giảm)
    @PostMapping("/update-cart-quantity")
    public String updateCartQuantity(@RequestParam("cartItemId") long cartItemId,
            @RequestParam("action") String action,
            @RequestParam("quantity") long quantity, // Lưu ý: Tham số này có trong code gốc của bạn không?
            HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        this.productService.handleUpdateCartQuantity(cartItemId, action, session);
        return "redirect:/cart";
    }

    // Xóa một sản phẩm cụ thể khỏi giỏ hàng dựa trên ID
    @GetMapping("/delete-cart-item/{id}")
    public String deleteCartItem(@PathVariable long id, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        this.productService.handleDeleteCartItem(id, session);
        return "redirect:/cart";
    }

    // Xóa danh sách nhiều sản phẩm khỏi giỏ hàng cùng một lúc
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