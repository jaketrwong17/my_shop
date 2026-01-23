package com.example.shop.controller.admin;

import com.example.shop.domain.Category;
import com.example.shop.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/admin/category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // 1. Hiển thị danh sách & Tìm kiếm
    @GetMapping
    public String getCategoryPage(Model model, @RequestParam(value = "keyword", required = false) String keyword) {
        List<Category> list = categoryService.getAllCategories(keyword);
        model.addAttribute("categories", list);
        model.addAttribute("keyword", keyword);
        return "admin/category/show";
    }

    // 2. Trang thêm mới
    @GetMapping("/create")
    public String getCreatePage(Model model) {
        model.addAttribute("newCategory", new Category());
        return "admin/category/create";
    }

    @PostMapping("/create")
    public String createCategory(@ModelAttribute("newCategory") Category category) {
        categoryService.saveCategory(category);
        return "redirect:/admin/category";
    }

    // 3. Trang cập nhật
    @GetMapping("/update/{id}")
    public String getUpdatePage(Model model, @PathVariable long id) {
        Category category = categoryService.getCategoryById(id);
        model.addAttribute("newCategory", category);
        return "admin/category/update";
    }

    @PostMapping("/update")
    public String updateCategory(@ModelAttribute("newCategory") Category category) {
        categoryService.saveCategory(category);
        return "redirect:/admin/category";
    }

    // 4. Xóa danh mục
    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable long id) {
        categoryService.deleteCategory(id);
        return "redirect:/admin/category";
    }
}