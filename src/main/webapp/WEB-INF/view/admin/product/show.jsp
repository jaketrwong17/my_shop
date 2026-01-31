<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

            <!DOCTYPE html>
            <html lang="vi">
            <jsp:include page="../layout/header.jsp" />

            <style>
                /* 1. Giao diện chính của Modal kiểu Shopee */
                .shopee-modal-content {
                    display: flex;
                    background-color: #fff;
                    border-radius: 4px;
                    overflow: hidden;
                    min-height: 500px;
                }

                /* Vùng ảnh to bên trái */
                .shopee-main-view {
                    flex: 2;
                    background-color: #f5f5f5;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    position: relative;
                }

                .shopee-main-view img {
                    max-width: 100%;
                    max-height: 550px;
                    object-fit: contain;
                }

                /* Vùng danh sách ảnh nhỏ bên phải */
                .shopee-side-view {
                    flex: 1;
                    padding: 20px;
                    background-color: #fff;
                    border-left: 1px solid #eee;
                    max-height: 600px;
                    overflow-y: auto;
                }

                .thumb-item {
                    width: 80px;
                    height: 80px;
                    margin-bottom: 10px;
                    border: 2px solid transparent;
                    cursor: pointer;
                    border-radius: 2px;
                    transition: border-color 0.2s;
                }

                .thumb-item:hover {
                    border-color: #ee4d2d;
                }

                .thumb-item.active {
                    border-color: #ee4d2d;
                    opacity: 0.7;
                }

                .thumb-item img {
                    width: 100%;
                    height: 100%;
                    object-fit: cover;
                }

                /* Chỉnh nút chuyển ảnh cho nổi bật trên nền xám */
                .carousel-control-prev-icon,
                .carousel-control-next-icon {
                    filter: invert(1);
                }
            </style>

            <body>
                <div class="d-flex" id="wrapper">
                    <jsp:include page="../layout/sidebar.jsp">
                        <jsp:param name="active" value="product" />
                    </jsp:include>

                    <div id="page-content-wrapper">
                        <nav class="navbar navbar-expand-lg navbar-light bg-white border-bottom px-4 py-3">
                            <h4 class="mb-0 text-dark fw-bold">Quản lý Sản phẩm</h4>
                        </nav>
                        <c:if test="${not empty errorMessage}">
                            <div class="alert alert-danger alert-dismissible fade show m-3">
                                ${errorMessage}
                                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                            </div>
                        </c:if>

                        <div class="container-fluid px-4 py-4">
                            <div class="card shadow-sm border-0 rounded-3">
                                <div class="card-header bg-white py-3">
                                    <div class="row align-items-center">
                                        <div class="col-md-9">
                                            <form action="/admin/product" method="GET"
                                                class="d-flex gap-2 align-items-center">
                                                <div class="input-group" style="max-width: 300px;">
                                                    <select name="categoryId" class="form-select">
                                                        <option value="">-- Tất cả danh mục --</option>
                                                        <c:forEach var="cate" items="${categories}">
                                                            <option value="${cate.id}" ${categoryId==cate.id
                                                                ? 'selected' : '' }>
                                                                ${cate.name}
                                                            </option>
                                                        </c:forEach>
                                                    </select>

                                                </div>

                                                <input type="text" name="keyword" class="form-control" placeholder=""
                                                    value="${keyword}" style="max-width: 400px;">
                                                <button class="btn btn-outline-primary ms-2"><i
                                                        class="fas fa-search"></i>
                                                </button>
                                            </form>
                                        </div>
                                        <div class="col-md-3 text-end">
                                            <a href="/admin/product/create" class="btn btn-success fw-bold">
                                                <i class="fas fa-plus me-1"></i> THÊM SẢN PHẨM
                                            </a>
                                        </div>
                                    </div>
                                </div>

                                <div class="card-body p-0">
                                    <table class="table table-hover align-middle mb-0">
                                        <thead class="bg-light text-secondary">
                                            <tr>
                                                <th class="ps-4">ID</th>
                                                <th>Ảnh</th>
                                                <th>Tên sản phẩm</th>
                                                <th>Giá bán</th>
                                                <th>Danh mục</th>
                                                <th>Hành động</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="p" items="${products}">
                                                <tr>
                                                    <td class="ps-4 fw-bold">#${p.id}</td>
                                                    <td>
                                                        <c:if test="${not empty p.images}">
                                                            <div style="width: 60px; height: 60px;">
                                                                <img src="/images/${p.images[0].imageUrl}"
                                                                    class="img-thumbnail w-100 h-100 object-fit-cover">
                                                            </div>
                                                        </c:if>
                                                    </td>
                                                    <td class="fw-bold text-primary">
                                                        ${p.name}
                                                        <div class="mt-1">
                                                            <c:forEach var="color" items="${p.colors}">
                                                                <span class="badge border text-dark fw-normal bg-light"
                                                                    style="font-size: 0.7rem;">
                                                                    ${color.colorName}: <strong
                                                                        class="text-danger">${color.quantity}</strong>
                                                                </span>
                                                            </c:forEach>
                                                        </div>
                                                    </td>
                                                    <td class="text-success fw-bold">
                                                        <fmt:formatNumber value="${p.price}" type="currency"
                                                            currencySymbol="đ" />
                                                    </td>
                                                    <td><span class="badge bg-secondary">${p.category.name}</span></td>
                                                    <td>
                                                        <button type="button"
                                                            class="btn btn-sm btn-info text-white me-1"
                                                            data-bs-toggle="modal" data-bs-target="#imageModal"
                                                            data-name="${p.name}"
                                                            data-images="<c:forEach var='img' items='${p.images}' varStatus='status'>/images/${img.imageUrl}${!status.last ? ',' : ''}</c:forEach>">
                                                            <i class="fas fa-eye"></i>
                                                        </button>
                                                        <a href="/admin/product/update/${p.id}"
                                                            class="btn btn-sm btn-warning text-white me-1"><i
                                                                class="fas fa-edit"></i></a>
                                                        <a href="/admin/product/delete/${p.id}"
                                                            class="btn btn-sm btn-danger"
                                                            onclick="return confirm('Xóa sản phẩm này?')"><i
                                                                class="fas fa-trash"></i></a>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="modal fade" id="imageModal" tabindex="-1" aria-hidden="true">
                    <div class="modal-dialog modal-dialog-centered modal-xl">
                        <div class="modal-content border-0">
                            <div class="modal-header border-bottom bg-light">
                                <h5 class="modal-title fw-bold" id="modalProductName">Chi tiết ảnh sản phẩm</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                            </div>
                            <div class="modal-body p-0">
                                <div class="shopee-modal-content">
                                    <div class="shopee-main-view">
                                        <div id="productCarousel" class="carousel slide" data-bs-ride="false"
                                            data-bs-interval="false">
                                            <div class="carousel-inner" id="carouselInner"></div>
                                            <button class="carousel-control-prev" type="button"
                                                data-bs-target="#productCarousel" data-bs-slide="prev">
                                                <span class="carousel-control-prev-icon"></span>
                                            </button>
                                            <button class="carousel-control-next" type="button"
                                                data-bs-target="#productCarousel" data-bs-slide="next">
                                                <span class="carousel-control-next-icon"></span>
                                            </button>
                                        </div>
                                    </div>
                                    <div class="shopee-side-view">
                                        <h6 class="text-muted mb-3 small fw-bold">DANH SÁCH ẢNH</h6>
                                        <div id="thumbnailList" class="d-flex flex-wrap gap-2"></div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

                <script>
                    document.addEventListener("DOMContentLoaded", function () {
                        const imageModalEl = document.getElementById('imageModal');
                        if (imageModalEl) {
                            imageModalEl.addEventListener('show.bs.modal', event => {
                                const button = event.relatedTarget;
                                const imagesString = button.getAttribute('data-images');
                                const productName = button.getAttribute('data-name');
                                const carouselInner = document.getElementById('carouselInner');
                                const thumbnailList = document.getElementById('thumbnailList');
                                const modalTitle = document.getElementById('modalProductName');

                                modalTitle.innerText = productName;
                                carouselInner.innerHTML = "";
                                thumbnailList.innerHTML = "";

                                const images = imagesString.split(',').filter(img => img.trim() !== "");

                                if (images.length > 0) {
                                    images.forEach((imgSrc, index) => {
                                        const activeClass = index === 0 ? 'active' : '';

                                        carouselInner.innerHTML += `
                                <div class="carousel-item \${activeClass}">
                                    <img src="\${imgSrc}" class="d-block">
                                </div>`;

                                        const thumbDiv = document.createElement('div');
                                        thumbDiv.className = `thumb-item \${activeClass}`;
                                        thumbDiv.innerHTML = `<img src="\${imgSrc}">`;

                                        thumbDiv.addEventListener('click', () => {
                                            const carousel = new bootstrap.Carousel(document.getElementById('productCarousel'));
                                            carousel.to(index);
                                            document.querySelectorAll('.thumb-item').forEach(t => t.classList.remove('active'));
                                            thumbDiv.classList.add('active');
                                        });

                                        thumbnailList.appendChild(thumbDiv);
                                    });

                                    const myCarousel = document.getElementById('productCarousel');
                                    myCarousel.addEventListener('slid.bs.carousel', function (e) {
                                        const idx = e.to;
                                        document.querySelectorAll('.thumb-item').forEach((t, i) => {
                                            i === idx ? t.classList.add('active') : t.classList.remove('active');
                                        });
                                    });
                                }
                            });
                        }
                    });
                </script>
            </body>

            </html>