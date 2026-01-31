<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
        <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

            <!DOCTYPE html>
            <html lang="vi">
            <jsp:include page="../layout/header.jsp" />

            <style>
                .img-card {
                    position: relative;
                    width: 120px;
                    height: 120px;
                    border: 1px solid #ddd;
                    border-radius: 8px;
                    overflow: hidden;
                    background: #fff;
                    display: flex;
                    justify-content: center;
                    align-items: center;
                }

                .img-card img {
                    width: 100%;
                    height: 100%;
                    object-fit: cover;
                }

                .upload-btn-wrapper {
                    width: 120px;
                    height: 120px;
                    border: 2px dashed #0d6efd;
                    border-radius: 8px;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    cursor: pointer;
                    color: #0d6efd;
                    background: #f8f9fa;
                    transition: 0.2s;
                }

                .upload-btn-wrapper:hover {
                    background: #e9ecef;
                }

                .btn-delete-img {
                    position: absolute;
                    top: 5px;
                    right: 5px;
                    background: rgba(220, 53, 69, 0.9);
                    color: white;
                    border: none;
                    border-radius: 50%;
                    width: 24px;
                    height: 24px;
                    font-size: 12px;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    cursor: pointer;
                    z-index: 10;
                }

                .btn-delete-img:hover {
                    background: #c82333;
                }
            </style>

            <body>
                <div class="d-flex" id="wrapper">
                    <jsp:include page="../layout/sidebar.jsp">
                        <jsp:param name="active" value="category" />
                    </jsp:include>

                    <div id="page-content-wrapper">
                        <nav class="navbar navbar-expand-lg navbar-light bg-white border-bottom px-4 py-3">
                            <h4 class="mb-0 text-dark fw-bold">Cập nhật Danh mục</h4>
                        </nav>

                        <div class="container-fluid px-4 py-4">
                            <div class="row justify-content-center">
                                <div class="col-lg-6">
                                    <div class="card shadow-sm border-0 rounded-3">
                                        <div class="card-body p-4">
                                            <form:form action="/admin/category/update" method="POST"
                                                modelAttribute="newCategory" enctype="multipart/form-data">

                                                <form:hidden path="id" />

                                                <input type="hidden" id="isDeleteImage" name="isDeleteImage"
                                                    value="false">

                                                <div class="mb-3">
                                                    <label class="form-label fw-bold">Tên danh mục <span
                                                            class="text-danger">*</span></label>
                                                    <form:input path="name" class="form-control form-control-lg"
                                                        required="true" />
                                                </div>

                                                <div class="mb-3">
                                                    <label class="form-label fw-bold">Biểu tượng (Icon)</label>
                                                    <div class="d-flex gap-3 align-items-start">

                                                        <c:if test="${not empty newCategory.image}">
                                                            <div class="img-card" id="currentImageContainer">
                                                                <img src="/images/${newCategory.image}"
                                                                    alt="Icon hiện tại">
                                                                <button type="button" class="btn-delete-img"
                                                                    onclick="deleteCurrentImage()" title="Xóa ảnh này">
                                                                    <i class="fas fa-times"></i>
                                                                </button>
                                                            </div>
                                                        </c:if>

                                                        <div id="previewContainer" style="display: none;">
                                                            <div class="img-card">
                                                                <img id="imgPreview" src="" alt="Preview">
                                                                <button type="button" class="btn-delete-img"
                                                                    onclick="removePreview()" title="Bỏ chọn ảnh">
                                                                    <i class="fas fa-times"></i>
                                                                </button>
                                                            </div>
                                                        </div>

                                                        <label class="upload-btn-wrapper" for="imgFile"
                                                            id="uploadBtnLabel"
                                                            style="${not empty newCategory.image ? 'display:none;' : 'display:flex;'}">
                                                            <div class="text-center">
                                                                <i class="fas fa-cloud-upload-alt fa-2x mb-1"></i>
                                                                <div style="font-size: 12px;">Chọn ảnh</div>
                                                            </div>
                                                        </label>
                                                    </div>

                                                    <input type="file" id="imgFile" name="imgFile" accept="image/*"
                                                        style="display: none;" onchange="previewImage(this)" />

                                                    <div class="form-text mt-2 text-muted">
                                                        <i class="fas fa-info-circle me-1"></i>Để trống nếu muốn giữ
                                                        nguyên ảnh cũ.
                                                    </div>
                                                </div>

                                                <div class="mb-4">
                                                    <label class="form-label fw-bold">Mô tả</label>
                                                    <form:textarea path="description" class="form-control" rows="4" />
                                                </div>

                                                <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                                                    <a href="/admin/category" class="btn btn-light me-md-2">Hủy bỏ</a>
                                                    <button type="submit" class="btn btn-primary px-4">Cập nhật</button>
                                                </div>
                                            </form:form>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
                <script>
                    // Xóa ảnh cũ đang có trên server
                    function deleteCurrentImage() {
                        const currentImgContainer = document.getElementById('currentImageContainer');
                        if (currentImgContainer) {
                            // Không remove() khỏi DOM mà chỉ ẩn đi, phòng khi user muốn Undo (nếu code phức tạp hơn)
                            // Nhưng ở đây ta ẩn đi và set cờ xóa
                            currentImgContainer.style.display = 'none';
                        }

                        // Hiện nút upload để chọn ảnh thay thế
                        document.getElementById('uploadBtnLabel').style.display = 'flex';

                        // Set value input hidden = true để Controller biết cần set image = null
                        document.getElementById('isDeleteImage').value = "true";
                    }

                    // Xem trước ảnh khi chọn input file
                    function previewImage(input) {
                        if (input.files && input.files[0]) {
                            const reader = new FileReader();
                            reader.onload = function (e) {
                                // Gán src cho thẻ img preview
                                document.getElementById('imgPreview').src = e.target.result;

                                // Hiện container preview
                                document.getElementById('previewContainer').style.display = 'block';

                                // Ẩn nút upload
                                document.getElementById('uploadBtnLabel').style.display = 'none';

                                // Ẩn ảnh cũ đi (nếu đang hiện) vì đang định thay thế bằng ảnh mới
                                const currentImg = document.getElementById('currentImageContainer');
                                if (currentImg) currentImg.style.display = 'none';
                            }
                            reader.readAsDataURL(input.files[0]);
                        }
                    }

                    // Bỏ chọn ảnh vừa upload (Preview)
                    function removePreview() {
                        // Reset input file
                        document.getElementById('imgFile').value = "";

                        // Ẩn khung preview
                        document.getElementById('previewContainer').style.display = 'none';

                        // Logic hiển thị lại:
                        // Nếu trước đó ĐÃ bấm xóa ảnh cũ (isDeleteImage = true) -> Hiện nút upload
                        // Nếu trước đó CHƯA bấm xóa ảnh cũ -> Hiện lại ảnh cũ
                        const isDeleted = document.getElementById('isDeleteImage').value === "true";
                        const currentImg = document.getElementById('currentImageContainer');

                        if (currentImg && !isDeleted) {
                            // Trường hợp thay thế ảnh: Bỏ thay thế -> Hiện lại ảnh cũ
                            currentImg.style.display = 'flex'; // Dùng flex để căn giữa lại
                            document.getElementById('uploadBtnLabel').style.display = 'none';
                        } else {
                            // Trường hợp thêm mới hoặc đã xóa ảnh cũ -> Hiện nút upload
                            document.getElementById('uploadBtnLabel').style.display = 'flex';
                        }
                    }
                </script>
            </body>

            </html>