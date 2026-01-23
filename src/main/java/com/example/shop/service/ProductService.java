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
        User user = this.userService.getUserByEmail(email);
        if (user != null) {
            // 1. Lấy Cart của User, nếu chưa có thì tạo mới
            Cart cart = this.cartRepository.findByUser(user);
            if (cart == null) {
                Cart newCart = new Cart();
                newCart.setUser(user);
                newCart.setSum(0);
                cart = this.cartRepository.save(newCart);
            }

            // 2. Tìm sản phẩm khách muốn mua
            Product p = this.productRepository.findById(productId).get();

            // 3. Kiểm tra xem sản phẩm này đã có trong giỏ chưa
            CartItem oldDetail = this.cartItemRepository.findByCartAndProduct(cart, p);

            if (oldDetail == null) {
                // Trường hợp chưa có: Tạo mới chi tiết giỏ hàng
                CartItem cd = new CartItem();
                cd.setCart(cart);
                cd.setProduct(p);
                cd.setPrice(p.getPrice());
                cd.setQuantity(quantity);
                this.cartItemRepository.save(cd);

                // Cập nhật tổng số loại SP (Sum) hiện trên icon giỏ hàng
                int s = cart.getSum() + 1;
                cart.setSum(s);
                this.cartRepository.save(cart);
                session.setAttribute("sum", s);
            } else {
                // Trường hợp đã có: Chỉ cộng dồn số lượng
                oldDetail.setQuantity(oldDetail.getQuantity() + quantity);
                this.cartItemRepository.save(oldDetail);
            }
        }
    }

    // Hàm lấy giỏ hàng để ItemController gọi được
    public Cart fetchCartByUserEmail(String email) {
        User user = this.userService.getUserByEmail(email);
        if (user != null) {
            return this.cartRepository.findByUser(user);
        }
        return null;
    }
}