<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

            <!DOCTYPE html>
            <html lang="vi">
            <jsp:include page="../layout/header.jsp" />

            <style>
                /* Phần ảnh Preview */
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

                .btn-delete-img {
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
                }

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

                #imageFiles {
                    display: none;
                }

                /* Phần thông số kỹ thuật */
                .spec-row {
                    transition: all 0.2s;
                }

                .spec-row:hover {
                    background-color: #f8f9fa;
                }

                .btn-remove-spec {
                    color: #dc3545;
                    border: none;
                    background: transparent;
                    transition: 0.2s;
                }

                .btn-remove-spec:hover {
                    color: #a71d2a;
                    transform: scale(1.2);
                }
            </style>

            <body class="bg-light">
                <div class="d-flex" id="wrapper">
                    <jsp:include page="../layout/sidebar.jsp">
                        <jsp:param name="active" value="product" />
                    </jsp:include>

                    <div id="page-content-wrapper">
                        <nav class="navbar navbar-expand-lg navbar-light bg-white border-bottom px-4 py-3">
                            <h4 class="mb-0 text-dark fw-bold">Thêm Sản Phẩm Mới & Thông Số</h4>
                        </nav>

                        <div class="container-fluid px-4 py-4">
                            <form:form action="/admin/product/create" method="POST" modelAttribute="newProduct"
                                enctype="multipart/form-data">
                                <div class="row">
                                    <div class="col-lg-7">
                                        <div class="card shadow-sm border-0 rounded-3 mb-4">
                                            <div class="card-body p-4">
                                                <h6 class="fw-bold mb-4 text-primary text-uppercase small">Thông tin
                                                    chung</h6>

                                                <div class="row mb-3">
                                                    <div class="col-md-8">
                                                        <label class="form-label fw-bold small">Tên sản phẩm *</label>
                                                        <form:input path="name" class="form-control"
                                                            placeholder="Nhập tên sản phẩm..." required="true" />
                                                    </div>
                                                    <div class="col-md-4">
                                                        <label class="form-label fw-bold small">Giá bán (VNĐ) *</label>
                                                        <form:input path="price" type="number" class="form-control"
                                                            placeholder="0" required="true" />
                                                    </div>
                                                </div>

                                                <div class="mb-4">
                                                    <label class="form-label fw-bold small mb-2">Hình ảnh sản
                                                        phẩm</label>
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
                                                        <label class="form-label fw-bold small">Danh mục</label>
                                                        <form:select path="category.id" class="form-select">
                                                            <form:options items="${categories}" itemValue="id"
                                                                itemLabel="name" />
                                                        </form:select>
                                                    </div>
                                                    <div class="col-md-6">
                                                        <label class="form-label fw-bold small">Hãng sản xuất</label>
                                                        <form:input path="factory" class="form-control"
                                                            placeholder="Ví dụ: Dreame, Xiaomi..." />
                                                    </div>
                                                </div>

                                                <div class="mb-3">
                                                    <label class="form-label fw-bold small">Mô tả ngắn</label>
                                                    <form:textarea path="shortDesc" class="form-control" rows="2"
                                                        placeholder="Tóm tắt đặc điểm nổi bật..." />
                                                </div>

                                                <div class="mb-0">
                                                    <label class="form-label fw-bold small">Chi tiết bài viết</label>
                                                    <form:textarea path="detailDesc" class="form-control" rows="6" />
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="col-lg-5">
                                        <div class="card shadow-sm border-0 rounded-3">
                                            <div
                                                class="card-header bg-white py-3 d-flex justify-content-between align-items-center">
                                                <h6 class="mb-0 fw-bold text-primary text-uppercase small">Thông số nổi
                                                    bật</h6>
                                                <div class="btn-group">
                                                    <button type="button" class="btn btn-sm btn-outline-primary"
                                                        onclick="addMultipleSpecs(5)">+5 Dòng</button>
                                                    <button type="button" class="btn btn-sm btn-primary ms-1"
                                                        id="btnAddSpec"><i class="fas fa-plus"></i></button>
                                                </div>
                                            </div>
                                            <div class="card-body p-3">
                                                <div id="specs-container">
                                                    <div class="row g-2 mb-2 spec-row align-items-center">
                                                        <div class="col-5">
                                                            <input type="text" name="specNames"
                                                                class="form-control form-control-sm"
                                                                placeholder="Tên thông số">
                                                        </div>
                                                        <div class="col-6">
                                                            <input type="text" name="specValues"
                                                                class="form-control form-control-sm"
                                                                placeholder="Giá trị">
                                                        </div>
                                                        <div class="col-1 text-center">
                                                            <button type="button" class="btn-remove-spec"
                                                                onclick="removeSpec(this)"><i
                                                                    class="fas fa-times-circle"></i></button>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="card-footer bg-white border-0 p-4">
                                                <button type="submit"
                                                    class="btn btn-primary w-100 py-2 fw-bold shadow-sm">
                                                    <i class="fas fa-save me-2"></i>LƯU SẢN PHẨM
                                                </button>
                                                <a href="/admin/product"
                                                    class="btn btn-link w-100 text-muted mt-2 small text-decoration-none">Quay
                                                    lại danh sách</a>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </form:form>
                        </div>
                    </div>
                </div>

                <div class="modal fade" id="previewModal" tabindex="-1" aria-hidden="true">
                    <div class="modal-dialog modal-dialog-centered">
                        <div class="modal-content bg-transparent border-0 text-center"><img id="modalImg" src=""
                                class="img-fluid rounded shadow"></div>
                    </div>
                </div>

                <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

                <script>
                    // --- QUẢN LÝ ẢNH ---
                    const dt = new DataTransfer();

                    function handleFileSelect(input) {
                        const files = input.files;
                        const gallery = document.getElementById('gallery');
                        const uploadBtn = document.querySelector('.upload-btn-wrapper');
                        if (files) {
                            Array.from(files).forEach(file => {
                                dt.items.add(file);
                                const reader = new FileReader();
                                reader.onload = function (e) {
                                    const div = document.createElement('div');
                                    div.className = 'img-card';
                                    // THOÁT LỖI TRẮNG ẢNH: Sử dụng dấu \ trước ký hiệu $
                                    div.innerHTML = `<img src="\${e.target.result}" onclick="viewImage(this.src)"><button type="button" class="btn-delete-img"><i class="fas fa-times"></i></button>`;
                                    div.querySelector('.btn-delete-img').onclick = function () { div.remove(); removeFileFromDT(file); };
                                    gallery.insertBefore(div, uploadBtn);
                                }
                                reader.readAsDataURL(file);
                            });
                        }
                        input.files = dt.files;
                    }

                    function removeFileFromDT(fileToRemove) {
                        const newDt = new DataTransfer();
                        for (let i = 0; i < dt.files.length; i++) {
                            if (dt.files[i] !== fileToRemove) newDt.items.add(dt.files[i]);
                        }
                        dt.items.clear();
                        for (let i = 0; i < newDt.files.length; i++) dt.items.add(newDt.files[i]);
                        document.getElementById('imageFiles').files = dt.files;
                    }

                    // --- QUẢN LÝ THÔNG SỐ (SPECS) ---
                    function createSpecRowHTML() {
                        return `
                <div class="row g-2 mb-2 spec-row align-items-center">
                    <div class="col-5"><input type="text" name="specNames" class="form-control form-control-sm" placeholder="Tên thông số"></div>
                    <div class="col-6"><input type="text" name="specValues" class="form-control form-control-sm" placeholder="Giá trị"></div>
                    <div class="col-1 text-center"><button type="button" class="btn-remove-spec" onclick="removeSpec(this)"><i class="fas fa-times-circle"></i></button></div>
                </div>`;
                    }

                    document.getElementById('btnAddSpec').addEventListener('click', function () {
                        document.getElementById('specs-container').insertAdjacentHTML('beforeend', createSpecRowHTML());
                    });

                    function addMultipleSpecs(count) {
                        for (let i = 0; i < count; i++) {
                            document.getElementById('specs-container').insertAdjacentHTML('beforeend', createSpecRowHTML());
                        }
                    }

                    function removeSpec(btn) {
                        if (document.querySelectorAll('.spec-row').length > 1) {
                            btn.closest('.spec-row').remove();
                        }
                    }

                    function viewImage(src) {
                        document.getElementById('modalImg').src = src;
                        new bootstrap.Modal(document.getElementById('previewModal')).show();
                    }
                </script>
            </body>

            </html>