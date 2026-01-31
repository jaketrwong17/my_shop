package com.example.shop.controller.client;

import com.example.shop.domain.Cart;
import com.example.shop.domain.CartItem;
import com.example.shop.domain.Product;
import com.example.shop.service.CategoryService; // <--- Mới thêm
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
    private final CategoryService categoryService; // <--- Khai báo Service danh mục

    // Inject cả ProductService và CategoryService vào Constructor
    public ItemController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping("/product/{id}")
    public String getProductDetail(Model model, @PathVariable long id) {
        Product product = productService.fetchProductById(id).get();
        model.addAttribute("product", product);

        // <--- LẤY DANH MỤC ĐỂ HIỆN TRÊN HEADER --->
        model.addAttribute("categories", categoryService.getAllCategories(null));

        return "client/product/detail";
    }

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

        // <--- LẤY DANH MỤC ĐỂ HIỆN TRÊN HEADER (TRANG GIỎ HÀNG) --->
        model.addAttribute("categories", categoryService.getAllCategories(null));

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