<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

            <!DOCTYPE html>
            <html lang="vi">
            <jsp:include page="../layout/header.jsp" />
            <style>
                /* CSS cho phần Ảnh */
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
                    border-radius: 5px;
                    overflow: hidden;
                }

                .img-card img {
                    width: 100%;
                    height: 100%;
                    object-fit: cover;
                    cursor: pointer;
                }

                .btn-delete-img {
                    position: absolute;
                    top: 0;
                    right: 0;
                    background: rgba(255, 0, 0, 0.8);
                    color: white;
                    border: none;
                    width: 20px;
                    height: 20px;
                    font-size: 10px;
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
                    border-radius: 5px;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    cursor: pointer;
                    color: #0d6efd;
                    background: #f8f9fa;
                }

                #imageFiles {
                    display: none;
                }

                /* CSS cho phần Thông số kỹ thuật */
                .spec-row {
                    transition: all 0.2s;
                }

                .spec-row:hover {
                    background-color: #f8f9fa;
                }
            </style>

            <body>
                <div class="d-flex" id="wrapper">
                    <jsp:include page="../layout/sidebar.jsp">
                        <jsp:param name="active" value="product" />
                    </jsp:include>

                    <div id="page-content-wrapper">
                        <nav class="navbar navbar-expand-lg navbar-light bg-white border-bottom px-4 py-3">
                            <h4 class="mb-0 fw-bold">Cập nhật Sản phẩm & Thông số</h4>
                        </nav>

                        <div class="container-fluid px-4 py-4">
                            <form:form action="/admin/product/update" method="POST" modelAttribute="newProduct"
                                enctype="multipart/form-data">
                                <form:hidden path="id" />

                                <div class="row">
                                    <div class="col-lg-7">
                                        <div class="card shadow-sm border-0 mb-4">
                                            <div class="card-body p-4">
                                                <h6 class="fw-bold mb-3 text-primary">Thông tin chung</h6>
                                                <div class="row mb-3">
                                                    <div class="col-md-8">
                                                        <label class="form-label fw-bold">Tên sản phẩm</label>
                                                        <form:input path="name" class="form-control" required="true" />
                                                    </div>
                                                    <div class="col-md-4">
                                                        <label class="form-label fw-bold">Giá bán</label>
                                                        <form:input path="price" type="number" class="form-control"
                                                            required="true" />
                                                    </div>
                                                </div>
                                                <div class="mb-3">
                                                    <label class="form-label fw-bold">Hình ảnh sản phẩm</label>
                                                    <div class="gallery-wrap" id="gallery">
                                                        <c:forEach var="img" items="${newProduct.images}">
                                                            <div class="img-card" id="old-img-${img.id}">
                                                                <img src="/images/${img.imageUrl}"
                                                                    onclick="viewImage(this.src)">
                                                                <button type="button" class="btn-delete-img"
                                                                    onclick="removeOldImage('${img.id}')"><i
                                                                        class="fas fa-times"></i></button>
                                                            </div>
                                                        </c:forEach>
                                                        <label class="upload-btn-wrapper" for="imageFiles"><i
                                                                class="fas fa-plus fa-2x"></i></label>
                                                    </div>
                                                    <input type="file" id="imageFiles" name="imageFiles"
                                                        accept="image/*" multiple onchange="handleFileSelect(this)">
                                                    <div id="deleteContainer"></div>
                                                </div>
                                                <div class="mb-3">
                                                    <label class="form-label fw-bold">Danh mục</label>
                                                    <form:select path="category.id" class="form-select">
                                                        <form:options items="${categories}" itemValue="id"
                                                            itemLabel="name" />
                                                    </form:select>
                                                </div>
                                                <div class="mb-3">
                                                    <label class="form-label fw-bold">Mô tả ngắn</label>
                                                    <form:textarea path="shortDesc" class="form-control" rows="2" />
                                                </div>
                                                <div class="mb-0">
                                                    <label class="form-label fw-bold">Chi tiết bài viết</label>
                                                    <form:textarea path="detailDesc" class="form-control" rows="5" />
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="col-lg-5">
                                        <div class="card shadow-sm border-0">
                                            <div
                                                class="card-header bg-white py-3 d-flex justify-content-between align-items-center">
                                                <h6 class="mb-0 fw-bold text-primary">Thông số kỹ thuật</h6>
                                                <div class="btn-group">
                                                    <button type="button" class="btn btn-sm btn-outline-primary"
                                                        onclick="addMultipleRows(5)">+5 Dòng</button>
                                                    <button type="button" class="btn btn-sm btn-primary"
                                                        id="btnAddSpec"><i class="fas fa-plus"></i> Thêm</button>
                                                </div>
                                            </div>
                                            <div class="card-body p-3">
                                                <div id="specs-container">
                                                    <c:forEach var="spec" items="${newProduct.specs}">
                                                        <div class="row g-2 mb-2 spec-row align-items-center">
                                                            <div class="col-5">
                                                                <input type="text" name="specNames"
                                                                    class="form-control form-control-sm"
                                                                    value="${spec.specName}" placeholder="Tên thông số">
                                                            </div>
                                                            <div class="col-6">
                                                                <input type="text" name="specValues"
                                                                    class="form-control form-control-sm"
                                                                    value="${spec.specValue}" placeholder="Giá trị">
                                                            </div>
                                                            <div class="col-1">
                                                                <button type="button"
                                                                    class="text-danger border-0 bg-transparent"
                                                                    onclick="removeSpec(this)"><i
                                                                        class="fas fa-trash-alt"></i></button>
                                                            </div>
                                                        </div>
                                                    </c:forEach>

                                                    <c:if test="${empty newProduct.specs}">
                                                        <div class="row g-2 mb-2 spec-row align-items-center">
                                                            <div class="col-5"><input type="text" name="specNames"
                                                                    class="form-control form-control-sm"
                                                                    placeholder="VD: Lực hút"></div>
                                                            <div class="col-6"><input type="text" name="specValues"
                                                                    class="form-control form-control-sm"
                                                                    placeholder="VD: 8000 Pa"></div>
                                                            <div class="col-1"><button type="button"
                                                                    class="text-danger border-0 bg-transparent"
                                                                    onclick="removeSpec(this)"><i
                                                                        class="fas fa-trash-alt"></i></button></div>
                                                        </div>
                                                    </c:if>
                                                </div>
                                            </div>
                                            <div class="card-footer bg-light p-3">
                                                <button type="submit"
                                                    class="btn btn-warning w-100 text-white fw-bold">CẬP NHẬT SẢN
                                                    PHẨM</button>
                                                <a href="/admin/product" class="btn btn-link w-100 text-muted mt-2">Quay
                                                    lại danh sách</a>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </form:form>
                        </div>
                    </div>
                </div>

                <div class="modal fade" id="previewModal" tabindex="-1">
                    <div class="modal-dialog modal-dialog-centered">
                        <div class="modal-content bg-transparent border-0 text-center"><img id="modalImg" src=""
                                class="img-fluid rounded shadow"></div>
                    </div>
                </div>

                <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
                <script>
                    // --- LOGIC XỬ LÝ ẢNH ---
                    function removeOldImage(id) {
                        if (confirm('Xóa ảnh này?')) {
                            document.getElementById('old-img-' + id).remove();
                            const input = document.createElement('input');
                            input.type = 'hidden'; input.name = 'deleteImageIds'; input.value = id;
                            document.getElementById('deleteContainer').appendChild(input);
                        }
                    }

                    const dt = new DataTransfer();
                    function handleFileSelect(input) {
                        const files = input.files;
                        const gallery = document.getElementById('gallery');
                        const uploadBtn = document.querySelector('.upload-btn-wrapper');
                        Array.from(files).forEach(file => {
                            dt.items.add(file);
                            const reader = new FileReader();
                            reader.onload = e => {
                                const div = document.createElement('div');
                                div.className = 'img-card';
                                div.innerHTML = '<img src="' + e.target.result + '" onclick="viewImage(this.src)"><button type="button" class="btn-delete-img"><i class="fas fa-times"></i></button>';
                                div.querySelector('.btn-delete-img').onclick = () => { div.remove(); removeFileFromDT(file); };
                                gallery.insertBefore(div, uploadBtn);
                            };
                            reader.readAsDataURL(file);
                        });
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

                    // --- LOGIC XỬ LÝ THÔNG SỐ (SPECS) ---
                    function createSpecRowHTML() {
                        return `
                <div class="row g-2 mb-2 spec-row align-items-center">
                    <div class="col-5"><input type="text" name="specNames" class="form-control form-control-sm" placeholder="Tên thông số"></div>
                    <div class="col-6"><input type="text" name="specValues" class="form-control form-control-sm" placeholder="Giá trị"></div>
                    <div class="col-1"><button type="button" class="text-danger border-0 bg-transparent" onclick="removeSpec(this)"><i class="fas fa-trash-alt"></i></button></div>
                </div>`;
                    }

                    document.getElementById('btnAddSpec').addEventListener('click', function () {
                        document.getElementById('specs-container').insertAdjacentHTML('beforeend', createSpecRowHTML());
                    });

                    function addMultipleRows(count) {
                        for (let i = 0; i < count; i++) {
                            document.getElementById('specs-container').insertAdjacentHTML('beforeend', createSpecRowHTML());
                        }
                    }

                    function removeSpec(btn) {
                        btn.closest('.spec-row').remove();
                    }

                    function viewImage(src) {
                        document.getElementById('modalImg').src = src;
                        new bootstrap.Modal(document.getElementById('previewModal')).show();
                    }
                </script>
            </body>

            </html>