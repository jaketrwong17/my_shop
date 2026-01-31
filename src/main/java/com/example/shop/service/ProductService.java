package com.example.shop.service;

import com.example.shop.domain.Cart;
import com.example.shop.domain.CartItem;
import com.example.shop.domain.Product;
import com.example.shop.domain.ProductColor;
import com.example.shop.domain.User;
import com.example.shop.repository.CartItemRepository;
import com.example.shop.repository.CartRepository;
import com.example.shop.repository.ProductColorRepository;
import com.example.shop.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.util.ArrayList;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final ProductColorRepository productColorRepository;
    private final UserService userService;

    public ProductService(CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            ProductRepository productRepository,
            ProductColorRepository productColorRepository,
            UserService userService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.productColorRepository = productColorRepository;
        this.userService = userService;
    }

    // ==================== QUẢN LÝ SẢN PHẨM (LOGIC CHUẨN) ====================

    // 1. Hàm hỗ trợ: Tính tổng số lượng từ các biến thể màu sắc
    private void enrichProductQuantity(Product product) {
        // Kiểm tra xem sản phẩm có danh sách màu không
        if (product.getColors() != null && !product.getColors().isEmpty()) {
            long total = product.getColors().stream()
                    .mapToLong(ProductColor::getQuantity)
                    .sum();
            // Nếu có màu, gán tổng số lượng các màu vào sản phẩm
            product.setQuantity(total);
        }
        // Nếu không có màu, giữ nguyên quantity mặc định từ bảng products
    }

    // 2. Lấy danh sách sản phẩm (Tích hợp tìm kiếm + lọc + tính tổng)
    public List<Product> getAllProducts(String keyword, Long categoryId) {
        List<Product> products;

        // Logic tìm kiếm và lọc
        if (keyword != null && !keyword.isEmpty() && categoryId != null) {
            products = productRepository.findByNameContainingIgnoreCaseAndCategoryId(keyword, categoryId);
        } else if (keyword != null && !keyword.isEmpty()) {
            products = productRepository.findByNameContainingIgnoreCase(keyword);
        } else if (categoryId != null) {
            products = productRepository.findByCategoryId(categoryId);
        } else {
            products = productRepository.findAll();
        }

        // [QUAN TRỌNG] Duyệt qua từng sản phẩm để tính lại tổng số lượng
        for (Product p : products) {
            this.enrichProductQuantity(p);
        }

        return products;
    }

    public Product handleSaveProduct(Product product) {
        return productRepository.save(product);
    }

    // 3. Lấy chi tiết sản phẩm (Cũng phải tính tổng)
    public Optional<Product> fetchProductById(long id) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            this.enrichProductQuantity(productOptional.get());
        }
        return productOptional;
    }

    public void deleteProduct(long id) {
        productRepository.deleteById(id);
    }

    public List<Product> fetchProductsByName(String name) {
        List<Product> products = productRepository.findByNameContainingIgnoreCase(name);
        products.forEach(this::enrichProductQuantity); // Nhớ tính tổng ở đây nữa
        return products;
    }

    public List<Product> fetchProductsByCategory(Long categoryId) {
        List<Product> products = productRepository.findByCategoryId(categoryId);
        products.forEach(this::enrichProductQuantity); // Nhớ tính tổng ở đây nữa
        return products;
    }

    // ==================== QUẢN LÝ GIỎ HÀNG (GIỮ NGUYÊN) ====================

    public Cart fetchCartByUserEmail(String email) {
        User user = this.userService.getUserByEmail(email);
        if (user != null) {
            return this.cartRepository.findByUser(user);
        }
        return null;
    }

    public void handleAddProductToCart(String email, long productId, long colorId, HttpSession session, long quantity) {
        if (email == null) {
            // --- GUEST ---
            List<CartItem> guestCart = (List<CartItem>) session.getAttribute("guestCart");
            if (guestCart == null) {
                guestCart = new ArrayList<>();
            }
            Optional<Product> pOptional = this.productRepository.findById(productId);
            Optional<ProductColor> cOptional = this.productColorRepository.findById(colorId);

            if (pOptional.isPresent() && cOptional.isPresent()) {
                Product p = pOptional.get();
                ProductColor pc = cOptional.get();
                boolean isExist = false;
                for (CartItem item : guestCart) {
                    if (item.getProduct().getId() == productId &&
                            item.getProductColor() != null && item.getProductColor().getId() == colorId) {
                        item.setQuantity(item.getQuantity() + quantity);
                        isExist = true;
                        break;
                    }
                }
                if (!isExist) {
                    CartItem newItem = new CartItem();
                    newItem.setId(System.currentTimeMillis());
                    newItem.setProduct(p);
                    newItem.setProductColor(pc);
                    newItem.setQuantity(quantity);
                    newItem.setPrice(p.getPrice());
                    guestCart.add(newItem);
                }
                session.setAttribute("guestCart", guestCart);
                session.setAttribute("sum", guestCart.size());
            }
        } else {
            // --- USER ---
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
                Optional<ProductColor> cOptional = this.productColorRepository.findById(colorId);
                if (pOptional.isPresent() && cOptional.isPresent()) {
                    Product p = pOptional.get();
                    ProductColor pc = cOptional.get();
                    CartItem oldDetail = this.cartItemRepository.findByCartAndProductAndProductColor(cart, p, pc);
                    if (oldDetail == null) {
                        CartItem newItem = new CartItem();
                        newItem.setCart(cart);
                        newItem.setProduct(p);
                        newItem.setProductColor(pc);
                        newItem.setPrice(p.getPrice());
                        newItem.setQuantity(quantity);
                        this.cartItemRepository.save(newItem);
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

    public void handleUpdateCartQuantity(long cartItemId, String action, HttpSession session) {
        String email = (String) session.getAttribute("email");
        if (email == null) {
            List<CartItem> guestCart = (List<CartItem>) session.getAttribute("guestCart");
            if (guestCart != null) {
                for (CartItem item : guestCart) {
                    if (item.getId() == cartItemId) {
                        if (action.equals("plus"))
                            item.setQuantity(item.getQuantity() + 1);
                        else if (action.equals("minus") && item.getQuantity() > 1)
                            item.setQuantity(item.getQuantity() - 1);
                        break;
                    }
                }
                session.setAttribute("guestCart", guestCart);
            }
        } else {
            Optional<CartItem> cartItemOptional = this.cartItemRepository.findById(cartItemId);
            if (cartItemOptional.isPresent()) {
                CartItem cartItem = cartItemOptional.get();
                if (action.equals("plus"))
                    cartItem.setQuantity(cartItem.getQuantity() + 1);
                else if (action.equals("minus") && cartItem.getQuantity() > 1)
                    cartItem.setQuantity(cartItem.getQuantity() - 1);
                this.cartItemRepository.save(cartItem);
            }
        }
    }

    public void handleDeleteCartItem(long id, HttpSession session) {
        String email = (String) session.getAttribute("email");
        if (email == null) {
            List<CartItem> guestCart = (List<CartItem>) session.getAttribute("guestCart");
            if (guestCart != null) {
                guestCart.removeIf(item -> item.getId() == id);
                session.setAttribute("guestCart", guestCart);
                session.setAttribute("sum", guestCart.size());
            }
        } else {
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

    public Page<Product> getAllProductsWithPaging(Pageable pageable) {
        return productRepository.findAllSortedByStock(pageable);
    }
}