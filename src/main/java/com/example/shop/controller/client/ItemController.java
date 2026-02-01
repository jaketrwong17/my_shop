package com.example.shop.controller.client;

import com.example.shop.domain.Cart;
import com.example.shop.domain.CartItem;
import com.example.shop.domain.Product;
import com.example.shop.domain.Review;

import com.example.shop.service.CategoryService;
import com.example.shop.service.OrderService;
import com.example.shop.service.ProductService;
import com.example.shop.service.ReviewService;
import com.example.shop.service.UserService;
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
    private final CategoryService categoryService;
    private final OrderService orderService;
    private final ReviewService reviewService;
    private final UserService userService;

    public ItemController(ProductService productService, CategoryService categoryService,
            OrderService orderService, ReviewService reviewService, UserService userService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.orderService = orderService;
        this.reviewService = reviewService;
        this.userService = userService;
    }

    @GetMapping("/product/{id}")
    public String getProductDetail(Model model, @PathVariable long id, HttpServletRequest request) {
        Product product = productService.fetchProductById(id).orElse(null);
        if (product == null)
            return "redirect:/";

        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories(null));

        // 1. Lấy danh sách đánh giá của sản phẩm này
        List<Review> reviews = reviewService.getReviewsByProduct(product);
        model.addAttribute("reviews", reviews);

        // 2. Kiểm tra quyền đánh giá
        boolean canReview = false;
        boolean hasReviewed = false;

        HttpSession session = request.getSession(false);
        if (session != null) {
            String email = (String) session.getAttribute("email");
            if (email != null) {
                // Check xem user đã mua sản phẩm này và đơn hàng đã hoàn thành chưa
                boolean bought = orderService.hasUserBoughtProduct(email, id);
                // Check xem user đã đánh giá chưa
                hasReviewed = reviewService.hasUserReviewedProduct(email, id);

                // Chỉ cho đánh giá nếu ĐÃ MUA và CHƯA TỪNG ĐÁNH GIÁ
                if (bought && !hasReviewed) {
                    canReview = true;
                }
            }
        }
        model.addAttribute("canReview", canReview);
        model.addAttribute("hasReviewed", hasReviewed);

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

    // --- HÀM XỬ LÝ POST ĐÁNH GIÁ (CẬP NHẬT LOGIC) ---
    @PostMapping("/product/add-review")
    public String addReview(@RequestParam("productId") long productId,
            @RequestParam("content") String content,
            @RequestParam("rating") int rating,
            HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            String email = (String) session.getAttribute("email");
            if (email != null) {
                // Kiểm tra lại lần nữa trước khi lưu để bảo mật
                boolean hasReviewed = reviewService.hasUserReviewedProduct(email, productId);
                if (!hasReviewed) {
                    reviewService.saveReview(email, productId, content, rating);
                }
            }
        }
        return "redirect:/product/" + productId;
    }
}