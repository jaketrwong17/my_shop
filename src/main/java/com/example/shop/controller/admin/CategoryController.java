package com.example.shop.controller.admin;

import com.example.shop.domain.Category;
import com.example.shop.service.CategoryService;
import com.example.shop.service.UploadService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/admin/category")
public class CategoryController {

    private final CategoryService categoryService;
    private final UploadService uploadService;

    public CategoryController(CategoryService categoryService, UploadService uploadService) {
        this.categoryService = categoryService;
        this.uploadService = uploadService;
    }

    // 1. HIỂN THỊ DANH SÁCH + TÌM KIẾM
    @GetMapping
    public String getCategoryPage(Model model,
            @RequestParam(value = "keyword", required = false) String keyword) {
        // Gọi Service lấy danh sách (Service sẽ xử lý: nếu keyword null thì lấy hết,
        // nếu có thì tìm theo tên)
        List<Category> list = categoryService.getAllCategories(keyword);

        model.addAttribute("categories", list);
        model.addAttribute("keyword", keyword); // Truyền lại keyword để giữ giá trị trong ô input
        return "admin/category/show";
    }

    // 2. TRANG TẠO MỚI
    @GetMapping("/create")
    public String getCreatePage(Model model) {
        model.addAttribute("newCategory", new Category());
        return "admin/category/create";
    }

    // 3. XỬ LÝ TẠO MỚI
    @PostMapping("/create")
    public String createCategory(@ModelAttribute("newCategory") Category category,
            @RequestParam("imgFile") MultipartFile file) {
        // Upload ảnh nếu có
        if (!file.isEmpty()) {
            String fileName = uploadService.handleSaveUploadFile(file, "images"); // Lưu vào thư mục images
            category.setImage(fileName);
        }
        categoryService.saveCategory(category);
        return "redirect:/admin/category";
    }

    // 4. TRANG CẬP NHẬT
    @GetMapping("/update/{id}")
    public String getUpdatePage(Model model, @PathVariable long id) {
        Category category = categoryService.getCategoryById(id);
        model.addAttribute("newCategory", category);
        return "admin/category/update";
    }

    // 5. XỬ LÝ CẬP NHẬT (Có xử lý xóa ảnh cũ/giữ ảnh cũ)
    @PostMapping("/update")
    public String updateCategory(@ModelAttribute("newCategory") Category category,
            @RequestParam("imgFile") MultipartFile file,
            @RequestParam(value = "isDeleteImage", required = false) Boolean isDeleteImage) {

        Category currentCategory = categoryService.getCategoryById(category.getId());

        if (currentCategory != null) {
            currentCategory.setName(category.getName());
            currentCategory.setDescription(category.getDescription());

            // Nếu người dùng bấm nút xóa ảnh cũ
            if (Boolean.TRUE.equals(isDeleteImage)) {
                currentCategory.setImage(null);
            }

            // Nếu có upload ảnh mới (Ghi đè)
            if (!file.isEmpty()) {
                String fileName = uploadService.handleSaveUploadFile(file, "images");
                currentCategory.setImage(fileName);
            }

            categoryService.saveCategory(currentCategory);
        }
        return "redirect:/admin/category";
    }

    // 6. XÓA DANH MỤC
    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable long id) {
        categoryService.deleteCategory(id);
        return "redirect:/admin/category";
    }
}