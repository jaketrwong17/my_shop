package com.example.shop.controller.admin;

import com.example.shop.domain.Product;
import com.example.shop.domain.ProductImage;
import com.example.shop.domain.ProductSpec;
import com.example.shop.service.CategoryService;
import com.example.shop.service.ProductService;
import com.example.shop.service.UploadService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @GetMapping
    public String getProductPage(Model model,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId) {
        model.addAttribute("products", productService.getAllProducts(keyword, categoryId));
        model.addAttribute("categories", categoryService.getAllCategories(null));
        return "admin/product/show";
    }

    @GetMapping("/create")
    public String getCreatePage(Model model) {
        model.addAttribute("newProduct", new Product());
        model.addAttribute("categories", categoryService.getAllCategories(null));
        return "admin/product/create";
    }

    // CẬP NHẬT: Thêm @RequestParam cho specNames và specValues
    @PostMapping("/create")
    public String createProduct(@ModelAttribute("newProduct") Product product,
            @RequestParam("imageFiles") MultipartFile[] files,
            @RequestParam(value = "specNames", required = false) String[] specNames,
            @RequestParam(value = "specValues", required = false) String[] specValues) {

        // 1. Xử lý lưu ảnh
        saveImages(product, files);

        // 2. Xử lý lưu thông số kỹ thuật ( Specs )
        handleSpecs(product, specNames, specValues);

        productService.handleSaveProduct(product);
        return "redirect:/admin/product";
    }

    @GetMapping("/update/{id}")
    public String getUpdatePage(Model model, @PathVariable long id) {
        Product currentProduct = productService.fetchProductById(id).get();
        model.addAttribute("newProduct", currentProduct);
        model.addAttribute("categories", categoryService.getAllCategories(null));
        return "admin/product/update";
    }

    // CẬP NHẬT: Thêm tham số nhận mảng thông số từ Form
    @PostMapping("/update")
    public String updateProduct(@ModelAttribute("newProduct") Product product,
            @RequestParam("imageFiles") MultipartFile[] files,
            @RequestParam(value = "specNames", required = false) String[] specNames,
            @RequestParam(value = "specValues", required = false) String[] specValues,
            @RequestParam(value = "deleteImageIds", required = false) List<Long> deleteImageIds) {

        Product currentProduct = productService.fetchProductById(product.getId()).get();

        if (deleteImageIds != null && !deleteImageIds.isEmpty()) {
            currentProduct.getImages().removeIf(img -> deleteImageIds.contains(img.getId()));
        }

        saveImages(currentProduct, files);

        // Cập nhật thông tin cơ bản
        currentProduct.setName(product.getName());
        currentProduct.setPrice(product.getPrice());
        currentProduct.setCategory(product.getCategory());
        currentProduct.setShortDesc(product.getShortDesc());
        currentProduct.setDetailDesc(product.getDetailDesc());
        currentProduct.setFactory(product.getFactory());

        // 3. XỬ LÝ THÔNG SỐ KỸ THUẬT (Xóa cũ, nạp mới)
        currentProduct.getSpecs().clear(); // Hibernate sẽ tự xóa record mồ côi nhờ orphanRemoval=true
        handleSpecs(currentProduct, specNames, specValues);

        productService.handleSaveProduct(currentProduct);
        return "redirect:/admin/product";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable long id) {
        productService.deleteProduct(id);
        return "redirect:/admin/product";
    }

    // Hàm helper nạp thông số kỹ thuật
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
}