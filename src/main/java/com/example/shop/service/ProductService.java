package com.example.shop.service;

import com.example.shop.domain.Cart;
import com.example.shop.domain.CartItem;
import com.example.shop.domain.Product;
import com.example.shop.domain.ProductColor;
import com.example.shop.domain.User;
import com.example.shop.domain.dto.TopProductDTO;
import com.example.shop.repository.CartItemRepository;
import com.example.shop.repository.CartRepository;
import com.example.shop.repository.ProductColorRepository;
import com.example.shop.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort; // Bổ sung import Sort

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

    // Tính toán tổng số lượng sản phẩm dựa trên các biến thể màu sắc
    private void enrichProductQuantity(Product product) {
        if (product.getColors() != null && !product.getColors().isEmpty()) {
            long total = product.getColors().stream()
                    .mapToLong(ProductColor::getQuantity)
                    .sum();
            product.setQuantity(total);
        }
    }

    // Lấy danh sách sản phẩm theo từ khóa và danh mục (Hàm cũ giữ nguyên)
    public List<Product> getAllProducts(String keyword, Long categoryId) {
        List<Product> products;

        if (keyword != null && !keyword.isEmpty() && categoryId != null) {
            products = productRepository.findByNameContainingIgnoreCaseAndCategoryId(keyword, categoryId);
        } else if (keyword != null && !keyword.isEmpty()) {
            products = productRepository.findByNameContainingIgnoreCase(keyword);
        } else if (categoryId != null) {
            products = productRepository.findByCategoryId(categoryId);
        } else {
            products = productRepository.findAll();
        }

        for (Product p : products) {
            this.enrichProductQuantity(p);
        }

        return products;
    }

    // Hàm lấy sản phẩm có sắp xếp (Dùng cho Controller mới)
    // Trong ProductService.java

    public List<Product> getAllProducts(String keyword, Long categoryId, String status) {
        List<Product> products;

        // 1. Lấy danh sách theo Keyword và Category như cũ
        if (keyword != null && !keyword.isEmpty() && categoryId != null) {
            products = productRepository.findByNameContainingIgnoreCaseAndCategoryId(keyword, categoryId);
        } else if (keyword != null && !keyword.isEmpty()) {
            products = productRepository.findByNameContainingIgnoreCase(keyword);
        } else if (categoryId != null) {
            products = productRepository.findByCategoryId(categoryId);
        } else {
            products = productRepository.findAll();
        }

        // 2. Lọc tiếp theo Status (Xử lý bằng Java Code)
        if ("active".equals(status)) {
            // Chỉ lấy sản phẩm đang hoạt động (active = true)
            products = products.stream().filter(Product::isActive).toList();
        } else if ("inactive".equals(status)) {
            // Chỉ lấy sản phẩm ngừng kinh doanh (active = false)
            products = products.stream().filter(p -> !p.isActive()).toList();
        }
        // Nếu status là "all" hoặc null thì không lọc gì cả

        // 3. Tính toán số lượng tồn kho (Logic cũ của bạn)
        for (Product p : products) {
            this.enrichProductQuantity(p);
        }

        return products;
    }

    // Lưu thông tin sản phẩm
    public Product handleSaveProduct(Product product) {
        return productRepository.save(product);
    }

    // Tìm kiếm sản phẩm theo ID và cập nhật số lượng tổng
    public Optional<Product> fetchProductById(long id) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            this.enrichProductQuantity(productOptional.get());
        }
        return productOptional;
    }

    // Xóa sản phẩm theo ID
    public void deleteProduct(long id) {
        productRepository.deleteById(id);
    }

    // Tìm kiếm sản phẩm theo tên
    public List<Product> fetchProductsByName(String name) {
        List<Product> products = productRepository.findByNameContainingIgnoreCase(name);
        products.forEach(this::enrichProductQuantity);
        return products;
    }

    // Tìm kiếm sản phẩm theo danh mục
    public List<Product> fetchProductsByCategory(Long categoryId) {
        List<Product> products = productRepository.findByCategoryId(categoryId);
        products.forEach(this::enrichProductQuantity);
        return products;
    }

    // Lấy thông tin giỏ hàng thông qua email người dùng
    public Cart fetchCartByUserEmail(String email) {
        User user = this.userService.getUserByEmail(email);
        if (user != null) {
            return this.cartRepository.findByUser(user);
        }
        return null;
    }

    // Xử lý thêm sản phẩm vào giỏ hàng
    public void handleAddProductToCart(String email, long productId, long colorId, HttpSession session, long quantity) {
        if (email == null) {
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
            User user = this.userService.getUserByEmail(email);
            if (user != null) {
                Cart cart = this.cartRepository.findByUser(user);
                if (cart == null) {
                    cart = new Cart();
                    cart.setUser(user);
                    cart.setSum(0);
                    cart = this.cartRepository.save(cart);
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

    // Cập nhật số lượng sản phẩm trong giỏ hàng
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

    // Xóa sản phẩm khỏi giỏ hàng
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

    // Lấy danh sách sản phẩm có phân trang và sắp xếp (Dùng cho Controller mới)
    public Page<Product> getAllProductsWithPaging(Pageable pageable, String sort) {
        if (sort != null && !sort.equals("default")) {
            Sort s = Sort.by("id").descending();
            if ("price-asc".equals(sort))
                s = Sort.by("price").ascending();
            else if ("price-desc".equals(sort))
                s = Sort.by("price").descending();

            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), s);
        }
        return productRepository.findAll(pageable);
    }

    // Lấy danh sách sản phẩm bán chạy nhất
    public List<TopProductDTO> getBestSellingProducts(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return productRepository.findBestSellingProducts(pageable);
    }

    // Đếm tổng số lượng sản phẩm trong hệ thống
    public long countAllProducts() {
        return productRepository.count();
    }

    // Hàm đảo ngược trạng thái kinh doanh (Active <-> Inactive)
    public void toggleProductStatus(long id) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            // Đảo ngược trạng thái hiện tại (Đang bật -> Tắt, Đang tắt -> Bật)
            product.setActive(!product.isActive());
            productRepository.save(product);
        }
    }
}