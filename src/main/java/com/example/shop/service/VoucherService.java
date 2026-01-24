package com.example.shop.service;

import com.example.shop.domain.Category;
import com.example.shop.domain.Voucher;
import com.example.shop.repository.VoucherRepository;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VoucherService {
    // PHẢI có 2 dòng khai báo này
    private final VoucherRepository voucherRepository;
    private final CategoryService categoryService;

    // PHẢI có Constructor này để hết gạch đỏ
    public VoucherService(VoucherRepository voucherRepository, CategoryService categoryService) {
        this.voucherRepository = voucherRepository;
        this.categoryService = categoryService;
    }

    public List<Voucher> getAllVouchers() {
        return this.voucherRepository.findAll();
    }

    public void handleSaveVoucher(Voucher voucher, List<Long> categoryIds) {
        if (categoryIds != null && !voucher.isAll()) {
            List<Category> cats = new ArrayList<>();
            for (Long id : categoryIds) {
                // Gọi đúng hàm từ categoryService đã nạp ở trên
                cats.add(this.categoryService.getCategoryById(id));
            }
            voucher.setCategories(cats);
        }
        this.voucherRepository.save(voucher);
    }

    public void deleteVoucher(long id) {
        this.voucherRepository.deleteById(id);
    }

    // Thêm hàm này vào class VoucherService
    public Voucher getVoucherById(long id) {
        Optional<Voucher> voucherOptional = this.voucherRepository.findById(id);
        if (voucherOptional.isPresent()) {
            return voucherOptional.get();
        }
        return null;
    }

    public List<Voucher> getVouchersWithFilters(Long categoryId, String keyword) {
        List<Voucher> allVouchers = this.getAllVouchers(); // Lấy từ Repository

        return allVouchers.stream().filter(v -> {
            // 1. Lọc theo từ khóa (Code)
            boolean matchKeyword = (keyword == null || keyword.isEmpty()) ||
                    v.getCode().toLowerCase().contains(keyword.toLowerCase());

            // 2. Lọc theo Danh mục hoặc Toàn sàn
            boolean matchCategory = true;
            if (categoryId != null) {
                if (categoryId == -1) {
                    // Trường hợp người dùng chọn "Toàn bộ sàn"
                    matchCategory = v.isAll();
                } else {
                    // Trường hợp chọn một danh mục cụ thể
                    // Voucher đó phải thuộc danh mục này HOẶC là voucher toàn sàn (vì toàn sàn áp
                    // dụng cho mọi danh mục)
                    matchCategory = v.isAll() || v.getCategories().stream().anyMatch(c -> c.getId() == categoryId);
                }
            }

            return matchKeyword && matchCategory;
        }).toList();
    }
}
