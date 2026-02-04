package com.example.shop.controller.admin;

import com.example.shop.domain.Product;
import com.example.shop.domain.ProductColor;
import com.example.shop.domain.ProductImage;
import com.example.shop.domain.ProductSpec;
import com.example.shop.service.CategoryService;
import com.example.shop.service.ProductService;
import com.example.shop.service.UploadService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin/product")
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final UploadService uploadService;

    public ProductController(ProductService productService, CategoryService categoryService,
            UploadService uploadService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.uploadService = uploadService;
    }

    // Hiển thị danh sách sản phẩm có lọc theo từ khóa và danh mục
    // Trong ProductController.java

    @GetMapping
    public String getProductPage(Model model,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false, defaultValue = "all") String status) { // 1. Thêm tham số status

        // 2. Gọi hàm Service mới (sẽ sửa ở bước dưới)
        model.addAttribute("products", productService.getAllProducts(keyword, categoryId, status));

        model.addAttribute("categories", categoryService.getAllCategories(null));

        // 3. Truyền lại các giá trị để giữ trên form sau khi reload
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("status", status);

        return "admin/product/show";
    }

    // Hiển thị trang tạo mới sản phẩm
    @GetMapping("/create")
    public String getCreatePage(Model model) {
        model.addAttribute("newProduct", new Product());
        model.addAttribute("categories", categoryService.getAllCategories(null));
        return "admin/product/create";
    }

    // Xử lý lưu sản phẩm mới kèm ảnh, thông số và màu sắc
    @PostMapping("/create")
    public String createProduct(@ModelAttribute("newProduct") Product product,
            @RequestParam("imageFiles") MultipartFile[] files,
            @RequestParam(value = "specNames", required = false) String[] specNames,
            @RequestParam(value = "specValues", required = false) String[] specValues,
            @RequestParam(value = "colorNames", required = false) String[] colorNames,
            @RequestParam(value = "colorQuantities", required = false) Long[] colorQuantities) {
        saveImages(product, files);
        handleSpecs(product, specNames, specValues);
        handleColors(product, colorNames, colorQuantities);

        productService.handleSaveProduct(product);
        return "redirect:/admin/product";
    }

    // Hiển thị trang cập nhật sản phẩm theo ID
    @GetMapping("/update/{id}")
    public String getUpdatePage(Model model, @PathVariable long id) {
        Product currentProduct = productService.fetchProductById(id).get();
        model.addAttribute("newProduct", currentProduct);
        model.addAttribute("categories", categoryService.getAllCategories(null));
        return "admin/product/update";
    }

    // Xử lý cập nhật thông tin sản phẩm và đồng bộ dữ liệu liên quan
    @PostMapping("/update")
    public String updateProduct(@ModelAttribute("newProduct") Product product,
            @RequestParam("imageFiles") MultipartFile[] files,
            @RequestParam(value = "specNames", required = false) String[] specNames,
            @RequestParam(value = "specValues", required = false) String[] specValues,
            @RequestParam(value = "colorNames", required = false) String[] colorNames,
            @RequestParam(value = "colorQuantities", required = false) Long[] colorQuantities,
            @RequestParam(value = "deleteImageIds", required = false) List<Long> deleteImageIds) {

        Product currentProduct = productService.fetchProductById(product.getId()).get();

        if (deleteImageIds != null && !deleteImageIds.isEmpty()) {
            currentProduct.getImages().removeIf(img -> deleteImageIds.contains(img.getId()));
        }
        saveImages(currentProduct, files);

        currentProduct.setName(product.getName());
        currentProduct.setPrice(product.getPrice());
        currentProduct.setCategory(product.getCategory());
        currentProduct.setShortDesc(product.getShortDesc());
        currentProduct.setDetailDesc(product.getDetailDesc());
        currentProduct.setFactory(product.getFactory());

        currentProduct.getSpecs().clear();
        handleSpecs(currentProduct, specNames, specValues);

        currentProduct.getColors().clear();
        handleColors(currentProduct, colorNames, colorQuantities);

        productService.handleSaveProduct(currentProduct);
        return "redirect:/admin/product";
    }

    // Xử lý xóa sản phẩm và bắt lỗi ràng buộc dữ liệu
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable long id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa sản phẩm thành công!");
        } catch (Exception e) {
            // Sửa lại thông báo lỗi cho rõ ràng hơn
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Sản phẩm đã có lịch sử đơn hàng, không thể xóa! Vui lòng chọn 'Ngừng kinh doanh'.");
        }
        return "redirect:/admin/product";
    }

    // Helper: Xử lý nạp thông số kỹ thuật cho sản phẩm
    private void handleSpecs(Product product, String[] specNames, String[] specValues) {
        if (specNames != null && specValues != null) {
            for (int i = 0; i < specNames.length; i++) {
                if (specNames[i] != null && !specNames[i].trim().isEmpty()) {
                    ProductSpec spec = new ProductSpec();
                    spec.setSpecName(specNames[i]);
                    spec.setSpecValue(specValues[i]);
                    spec.setProduct(product);
                    product.getSpecs().add(spec);
                }
            }
        }
    }

    // Helper: Xử lý nạp danh sách màu sắc và số lượng tồn kho
    private void handleColors(Product product, String[] colorNames, Long[] colorQuantities) {
        if (colorNames != null && colorQuantities != null) {
            for (int i = 0; i < colorNames.length; i++) {
                if (colorNames[i] != null && !colorNames[i].trim().isEmpty()) {
                    ProductColor pc = new ProductColor();
                    pc.setColorName(colorNames[i]);
                    pc.setQuantity(i < colorQuantities.length ? colorQuantities[i] : 0);
                    pc.setProduct(product);
                    product.getColors().add(pc);
                }
            }
        }
    }

    // Helper: Xử lý tải lên và lưu danh sách ảnh sản phẩm
    private void saveImages(Product product, MultipartFile[] files) {
        if (product.getImages() == null)
            product.setImages(new ArrayList<>());
        for (MultipartFile file : files) {
            String fileName = uploadService.handleSaveUploadFile(file, "images");
            if (fileName != null) {
                ProductImage img = new ProductImage();
                img.setImageUrl(fileName);
                img.setProduct(product);
                product.getImages().add(img);
            }
        }
    }

    // --- (Thêm đoạn này vào) Xử lý Ngừng kinh doanh / Mở bán lại ---
    @GetMapping("/toggle-status/{id}")
    public String toggleProductStatus(@PathVariable long id, RedirectAttributes redirectAttributes) {
        productService.toggleProductStatus(id);
        redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật trạng thái kinh doanh của sản phẩm!");
        return "redirect:/admin/product";
    }
}