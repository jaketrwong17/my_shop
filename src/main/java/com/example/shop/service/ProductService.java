package com.example.shop.service;

import com.example.shop.domain.Cart;
import com.example.shop.domain.CartItem;
import com.example.shop.domain.Product;
import com.example.shop.domain.User;
import com.example.shop.repository.CartItemRepository;
import com.example.shop.repository.CartRepository;
import com.example.shop.repository.ProductRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {
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

    // ==================== QUẢN LÝ SẢN PHẨM ====================
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

    public Product handleSaveProduct(Product product) {
        return productRepository.save(product);
    }

    public Optional<Product> fetchProductById(long id) {
        return productRepository.findById(id);
    }

    public void deleteProduct(long id) {
        productRepository.deleteById(id);
    }

    public List<Product> fetchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Product> fetchProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    // ==================== QUẢN LÝ GIỎ HÀNG ====================

    public Cart fetchCartByUserEmail(String email) {
        User user = this.userService.getUserByEmail(email);
        if (user != null) {
            return this.cartRepository.findByUser(user);
        }
        return null;
    }

    public void handleAddProductToCart(String email, long productId, HttpSession session, long quantity) {
        if (email == null) {
            // --- GUEST (Lưu Session) ---
            List<CartItem> guestCart = (List<CartItem>) session.getAttribute("guestCart");
            if (guestCart == null) {
                guestCart = new ArrayList<>();
            }

            Optional<Product> pOptional = this.productRepository.findById(productId);
            if (pOptional.isPresent()) {
                Product p = pOptional.get();
                boolean isExist = false;

                for (CartItem item : guestCart) {
                    if (item.getProduct().getId() == productId) {
                        item.setQuantity(item.getQuantity() + quantity);
                        isExist = true;
                        break;
                    }
                }

                if (!isExist) {
                    CartItem newItem = new CartItem();
                    // MẸO: Gán ID của CartItem bằng ProductID để thao tác Xóa/Sửa bên view
                    newItem.setId(p.getId());
                    newItem.setProduct(p);
                    newItem.setQuantity(quantity);
                    newItem.setPrice(p.getPrice());
                    guestCart.add(newItem);
                }

                session.setAttribute("guestCart", guestCart);
                session.setAttribute("sum", guestCart.size());
            }
        } else {
            // --- USER (Lưu Database) ---
            User user = this.userService.getUserByEmail(email);
            if (user != null) {
                Cart cart = this.cartRepository.findByUser(user);
                if (cart == null) {
                    cart = new Cart();
                    cart.setUser(user);
                    cart.setSum(0);
                    this.cartRepository.save(cart);
                }

                Optional<Product> pOptional = this.productRepository.findById(productId);
                if (pOptional.isPresent()) {
                    Product p = pOptional.get();
                    CartItem oldDetail = this.cartItemRepository.findByCartAndProduct(cart, p);

                    if (oldDetail == null) {
                        CartItem newItem = new CartItem();
                        newItem.setCart(cart);
                        newItem.setProduct(p);
                        newItem.setPrice(p.getPrice());
                        newItem.setQuantity(quantity);
                        this.cartItemRepository.save(newItem);

                        // Cập nhật tổng số loại sản phẩm trong giỏ
                        int newSum = cart.getSum() + 1;
                        cart.setSum(newSum);
                        this.cartRepository.save(cart);
                        session.setAttribute("sum", newSum);
                    } else {
                        oldDetail.setQuantity(oldDetail.getQuantity() + quantity);
                        this.cartItemRepository.save(oldDetail);
                    }
                }
            }
        }
    }

    // Xử lý Tăng/Giảm số lượng (Đã gộp Guest và User)
    public void handleUpdateCartQuantity(long cartItemId, String action, HttpSession session) {
        String email = (String) session.getAttribute("email");

        if (email == null) {
            // --- GUEST ---
            List<CartItem> guestCart = (List<CartItem>) session.getAttribute("guestCart");
            if (guestCart != null) {
                for (CartItem item : guestCart) {
                    // Với Guest, cartItemId chính là ProductId (do mình gán ở trên)
                    if (item.getId() == cartItemId) {
                        if (action.equals("plus")) {
                            item.setQuantity(item.getQuantity() + 1);
                        } else if (action.equals("minus") && item.getQuantity() > 1) {
                            item.setQuantity(item.getQuantity() - 1);
                        }
                        break;
                    }
                }
                session.setAttribute("guestCart", guestCart);
            }
        } else {
            // --- USER ---
            Optional<CartItem> cartItemOptional = this.cartItemRepository.findById(cartItemId);
            if (cartItemOptional.isPresent()) {
                CartItem cartItem = cartItemOptional.get();
                if (action.equals("plus")) {
                    cartItem.setQuantity(cartItem.getQuantity() + 1);
                } else if (action.equals("minus") && cartItem.getQuantity() > 1) {
                    cartItem.setQuantity(cartItem.getQuantity() - 1);
                }
                this.cartItemRepository.save(cartItem);
            }
        }
    }

    // Xử lý Xóa sản phẩm khỏi giỏ (Đã gộp Guest và User)
    public void handleDeleteCartItem(long id, HttpSession session) {
        String email = (String) session.getAttribute("email");

        if (email == null) {
            // --- GUEST ---
            List<CartItem> guestCart = (List<CartItem>) session.getAttribute("guestCart");
            if (guestCart != null) {
                // Dùng Iterator để xóa an toàn trong vòng lặp
                Iterator<CartItem> iterator = guestCart.iterator();
                while (iterator.hasNext()) {
                    CartItem item = iterator.next();
                    if (item.getId() == id) {
                        iterator.remove();
                        break;
                    }
                }
                session.setAttribute("guestCart", guestCart);
                session.setAttribute("sum", guestCart.size());
            }
        } else {
            // --- USER ---
            Optional<CartItem> cartItemOptional = this.cartItemRepository.findById(id);
            if (cartItemOptional.isPresent()) {
                CartItem cartItem = cartItemOptional.get();
                Cart cart = cartItem.getCart();
                this.cartItemRepository.deleteById(id);

                if (cart.getSum() > 0) {
                    int newSum = cart.getSum() - 1;
                    cart.setSum(newSum);
                    this.cartRepository.save(cart);
                    session.setAttribute("sum", newSum);
                }
            }
        }
    }
}