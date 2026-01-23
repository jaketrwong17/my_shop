package com.example.shop.service;

import com.example.shop.domain.Cart;
import com.example.shop.domain.CartItem;
import com.example.shop.domain.Product;
import com.example.shop.domain.User;
import com.example.shop.repository.CartItemRepository;
import com.example.shop.repository.CartRepository;
import com.example.shop.repository.ProductRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    // Thêm vào đầu class ProductService
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserService userService;

    public ProductService(CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            ProductRepository productRepository,
            UserService userService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userService = userService;
    }

    // Lấy danh sách sản phẩm có lọc theo từ khóa và danh mục
    public List<Product> getAllProducts(String keyword, Long categoryId) {
        if (keyword != null && !keyword.isEmpty() && categoryId != null) {
            return productRepository.findByNameContainingIgnoreCaseAndCategoryId(keyword, categoryId);
        }
        if (keyword != null && !keyword.isEmpty()) {
            return productRepository.findByNameContainingIgnoreCase(keyword);
        }
        if (categoryId != null) {
            return productRepository.findByCategoryId(categoryId);
        }
        return productRepository.findAll();
    }

    // Lưu hoặc Cập nhật sản phẩm
    public Product handleSaveProduct(Product product) {
        return productRepository.save(product);
    }

    // Tìm kiếm theo ID (trả về Optional để tránh NullPointerException)
    public Optional<Product> fetchProductById(long id) {
        return productRepository.findById(id);
    }

    // Xóa sản phẩm
    public void deleteProduct(long id) {
        productRepository.deleteById(id);
    }

    public List<Product> fetchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Product> fetchProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    public void handleAddProductToCart(String email, long productId, HttpSession session, long quantity) {
        // 1. Tìm User qua email
        User user = this.userService.getUserByEmail(email);
        if (user != null) {
            // 2. Lấy Cart của User, nếu chưa có thì tạo mới
            Cart cart = this.cartRepository.findByUser(user);
            if (cart == null) {
                Cart newCart = new Cart();
                newCart.setUser(user);
                newCart.setSum(0);
                cart = this.cartRepository.save(newCart);
            }

            // 3. Tìm sản phẩm
            Product product = this.fetchProductById(productId).get();

            // 4. Kiểm tra sản phẩm đã có trong CartItem chưa?
            CartItem oldDetail = this.cartItemRepository.findByCartAndProduct(cart, product);

            if (oldDetail == null) {
                // Chưa có -> Tạo mới CartItem
                CartItem cd = new CartItem();
                cd.setCart(cart);
                cd.setProduct(product);
                cd.setPrice(product.getPrice());
                cd.setQuantity(quantity);
                this.cartItemRepository.save(cd);

                // Cập nhật tổng số lượng loại SP trong giỏ (Sum)
                int s = cart.getSum() + 1;
                cart.setSum(s);
                this.cartRepository.save(cart);
                session.setAttribute("sum", s);
            } else {
                // Đã có -> Cộng dồn số lượng
                oldDetail.setQuantity(oldDetail.getQuantity() + quantity);
                this.cartItemRepository.save(oldDetail);
            }
        }
    }

    public Cart fetchCartByUserEmail(String email) {
        User user = this.userService.getUserByEmail(email);
        if (user != null) {
            return this.cartRepository.findByUser(user);
        }
        return null;
    }
}