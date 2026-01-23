<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

            <!DOCTYPE html>
            <html lang="vi">
            <jsp:include page="../layout/header.jsp" />

            <style>
                /* Khu vực hiển thị ảnh preview */
                .gallery-wrap {
                    display: flex;
                    flex-wrap: wrap;
                    gap: 10px;
                }

                .img-card {
                    position: relative;
                    width: 100px;
                    height: 100px;
                    border: 1px solid #ddd;
                    border-radius: 8px;
                    overflow: hidden;
                    background: #fff;
                    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                }

                .img-card img {
                    width: 100%;
                    height: 100%;
                    object-fit: cover;
                    cursor: pointer;
                }

                /* Nút xóa ảnh preview */
                .btn-delete {
                    position: absolute;
                    top: 2px;
                    right: 2px;
                    background: rgba(220, 53, 69, 0.9);
                    color: white;
                    border: none;
                    width: 22px;
                    height: 22px;
                    border-radius: 50%;
                    font-size: 11px;
                    cursor: pointer;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    z-index: 10;
                    transition: transform 0.2s;
                }

                .btn-delete:hover {
                    transform: scale(1.1);
                    background: #dc3545;
                }

                /* Khung dấu cộng để bấm chọn file */
                .upload-btn-wrapper {
                    width: 100px;
                    height: 100px;
                    border: 2px dashed #0d6efd;
                    border-radius: 8px;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    cursor: pointer;
                    color: #0d6efd;
                    background-color: #f8f9fa;
                    transition: all 0.3s;
                }

                .upload-btn-wrapper:hover {
                    background-color: #e9ecef;
                    border-color: #0a58ca;
                }

                /* Ẩn input file gốc */
                #imageFiles {
                    display: none;
                }
            </style>

            <body>
                <div class="d-flex" id="wrapper">
                    <jsp:include page="../layout/sidebar.jsp">
                        <jsp:param name="active" value="product" />
                    </jsp:include>

                    <div id="page-content-wrapper">
                        <nav class="navbar navbar-expand-lg navbar-light bg-white border-bottom px-4 py-3">
                            <h4 class="mb-0 text-dark fw-bold">Thêm Sản Phẩm Mới</h4>
                        </nav>

                        <div class="container-fluid px-4 py-4">
                            <div class="row justify-content-center">
                                <div class="col-lg-9">
                                    <div class="card shadow-sm border-0 rounded-3">
                                        <div class="card-body p-4">
                                            <form:form action="/admin/product/create" method="POST"
                                                modelAttribute="newProduct" enctype="multipart/form-data">

                                                <div class="row mb-3">
                                                    <div class="col-md-6">
                                                        <label class="form-label fw-bold">Tên sản phẩm <span
                                                                class="text-danger">*</span></label>
                                                        <form:input path="name" class="form-control"
                                                            placeholder="Nhập tên sản phẩm..." required="true" />
                                                    </div>

                                                    <div class="col-md-6">
                                                        <label class="form-label fw-bold">Giá bán (VNĐ) <span
                                                                class="text-danger">*</span></label>
                                                        <form:input path="price" type="number" class="form-control"
                                                            placeholder="0" required="true" />
                                                    </div>
                                                </div>

                                                <div class="mb-4">
                                                    <label class="form-label fw-bold mb-2">Hình ảnh sản phẩm</label>
                                                    <div class="gallery-wrap" id="gallery">
                                                        <label class="upload-btn-wrapper" for="imageFiles">
                                                            <i class="fas fa-plus fa-2x"></i>
                                                        </label>
                                                    </div>
                                                    <input type="file" id="imageFiles" name="imageFiles"
                                                        accept="image/*" multiple onchange="handleFileSelect(this)">
                                                    <small class="text-muted mt-2 d-block">Có thể chọn nhiều ảnh cùng
                                                        lúc.</small>
                                                </div>

                                                <div class="row mb-3">
                                                    <div class="col-md-6">
                                                        <label class="form-label fw-bold">Danh mục sản phẩm</label>
                                                        <form:select path="category.id" class="form-select">
                                                            <form:options items="${categories}" itemValue="id"
                                                                itemLabel="name" />
                                                        </form:select>
                                                    </div>
                                                </div>

                                                <div class="mb-3">
                                                    <label class="form-label fw-bold">Mô tả ngắn</label>
                                                    <form:textarea path="shortDesc" class="form-control" rows="2"
                                                        placeholder="Tóm tắt đặc điểm nổi bật..." />
                                                </div>

                                                <div class="mb-4">
                                                    <label class="form-label fw-bold">Chi tiết sản phẩm</label>
                                                    <form:textarea path="detailDesc" class="form-control" rows="5"
                                                        placeholder="Thông số kỹ thuật, công dụng chi tiết..." />
                                                </div>

                                                <div class="d-flex justify-content-end gap-2">
                                                    <a href="/admin/product" class="btn btn-outline-secondary px-4">Hủy
                                                        bỏ</a>
                                                    <button type="submit" class="btn btn-primary px-4">
                                                        <i class="fas fa-save me-2"></i>Lưu sản phẩm
                                                    </button>
                                                </div>
                                            </form:form>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="modal fade" id="previewModal" tabindex="-1" aria-hidden="true">
                    <div class="modal-dialog modal-dialog-centered">
                        <div class="modal-content bg-transparent border-0">
                            <div class="modal-body text-center p-0">
                                <img id="modalImg" src="" class="img-fluid rounded shadow" style="max-height: 85vh;">
                            </div>
                        </div>
                    </div>
                </div>

                <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

                <script>
                    // Đối tượng DataTransfer để quản lý danh sách file trong input
                    const dt = new DataTransfer();

                    function handleFileSelect(input) {
                        const files = input.files;
                        const gallery = document.getElementById('gallery');
                        const uploadBtn = document.querySelector('.upload-btn-wrapper');

                        if (files) {
                            Array.from(files).forEach(file => {
                                // Thêm vào danh sách file thực tế sẽ gửi lên Server
                                dt.items.add(file);

                                // Tạo giao diện Preview cho người dùng xem
                                const reader = new FileReader();
                                reader.onload = function (e) {
                                    const div = document.createElement('div');
                                    div.className = 'img-card';

                                    const img = document.createElement('img');
                                    img.src = e.target.result;
                                    img.title = file.name;
                                    img.onclick = function () { viewImage(this.src) };

                                    const btn = document.createElement('button');
                                    btn.type = 'button';
                                    btn.className = 'btn-delete';
                                    btn.innerHTML = '<i class="fas fa-times"></i>';

                                    // Xử lý khi bấm nút xóa ảnh preview
                                    btn.onclick = function () {
                                        div.remove();
                                        removeFileFromDataTransfer(file);
                                    };

                                    div.appendChild(img);
                                    div.appendChild(btn);

                                    // Chèn ảnh mới vào trước nút "+"
                                    gallery.insertBefore(div, uploadBtn);
                                }
                                reader.readAsDataURL(file);
                            });
                        }
                        // Đồng bộ danh sách file ảo vào input file thật
                        input.files = dt.files;
                    }

                    function removeFileFromDataTransfer(fileToRemove) {
                        const newDt = new DataTransfer();
                        for (let i = 0; i < dt.files.length; i++) {
                            const file = dt.files[i];
                            if (file !== fileToRemove) {
                                newDt.items.add(file);
                            }
                        }
                        // Xóa sạch dt cũ và copy từ newDt sang
                        dt.items.clear();
                        for (let i = 0; i < newDt.files.length; i++) {
                            dt.items.add(newDt.files[i]);
                        }
                        // Cập nhật lại input
                        document.getElementById('imageFiles').files = dt.files;
                    }

                    function viewImage(src) {
                        document.getElementById('modalImg').src = src;
                        new bootstrap.Modal(document.getElementById('previewModal')).show();
                    }
                </script>
            </body>

            </html>