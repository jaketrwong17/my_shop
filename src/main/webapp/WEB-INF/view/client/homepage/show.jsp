<%-- DÒNG QUAN TRỌNG NHẤT: Sửa lỗi vỡ font chữ --%>
    <%@page contentType="text/html" pageEncoding="UTF-8" %>
        <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
            <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

                <!DOCTYPE html>
                <html lang="vi">

                <head>
                    <meta charset="UTF-8">
                    <title>WolfHome - Thế giới Robot hút bụi</title>
                    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
                        rel="stylesheet">
                    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css"
                        rel="stylesheet">
                    <link rel="stylesheet" href="/client/css/style.css">
                </head>

                <body class="bg-light">
                    <%-- Header chứa Thanh tìm kiếm và Danh mục --%>
                        <jsp:include page="../layout/header.jsp" />

                        <div class="container my-5">
                            <div class="section-header mb-4 d-flex justify-content-between align-items-center">
                                <h3 class="fw-bold text-uppercase border-start border-primary border-4 ps-3">Sản phẩm
                                    mới nhất</h3>
                                <div class="filter-sort">
                                    <small class="text-muted">Sắp xếp theo: </small>
                                    <select class="form-select-sm border-0 bg-transparent">
                                        <option>Hàng mới nhất</option>
                                        <option>Giá tăng dần</option>
                                    </select>
                                </div>
                            </div>

                            <%-- LƯỚI SẢN PHẨM: Tái hiện mẫu WolfHome --%>
                                <div class="row row-cols-1 row-cols-sm-2 row-cols-md-3 row-cols-lg-5 g-4">
                                    <c:forEach var="p" items="${products}">
                                        <div class="col">
                                            <div class="card h-100 product-card shadow-sm border-0">
                                                <%-- Tag giảm giá như mẫu --%>
                                                    <span
                                                        class="badge bg-danger position-absolute top-0 start-0 m-2">Giảm
                                                        20%</span>

                                                    <%-- Link dẫn tới Use Case: Xem chi tiết sản phẩm --%>
                                                        <a href="/product/${p.id}"
                                                            class="text-decoration-none text-dark d-flex flex-column h-100">
                                                            <div class="p-3 text-center">
                                                                <%-- Lưu ý: Kiểm tra lại list images trong domain
                                                                    Product của bạn --%>
                                                                    <img src="/images/${p.images[0].imageUrl}"
                                                                        class="img-fluid rounded" alt="${p.name}"
                                                                        style="max-height: 180px;">
                                                            </div>
                                                            <div class="card-body pt-0 mt-auto">
                                                                <h6 class="card-title fw-bold text-truncate-2 mb-2">
                                                                    ${p.name}</h6>
                                                                <div class="price-box mb-2">
                                                                    <span class="text-danger fw-bold fs-5">
                                                                        <fmt:formatNumber value="${p.price}"
                                                                            type="currency" currencySymbol="đ" />
                                                                    </span>
                                                                </div>
                                                                <div class="rating-box small text-warning">
                                                                    <i class="fas fa-star"></i><i
                                                                        class="fas fa-star"></i><i
                                                                        class="fas fa-star"></i><i
                                                                        class="fas fa-star"></i><i
                                                                        class="fas fa-star"></i>
                                                                    <span class="text-muted ms-1">(12)</span>
                                                                </div>
                                                            </div>
                                                        </a>

                                                        <div class="card-footer bg-transparent border-0 pb-3">
                                                            <%-- Sửa action cho khớp với @PostMapping("/{id}") --%>
                                                                <form action="/add-product-to-cart/${p.id}"
                                                                    method="POST">
                                                                    <%-- Gửi mặc định số lượng là 1 --%>
                                                                        <input type="hidden" name="quantity" value="1">

                                                                        <button type="submit"
                                                                            class="btn btn-primary w-100 rounded-pill py-2 shadow-sm">
                                                                            <i class="fas fa-cart-plus me-1"></i> Thêm
                                                                            vào giỏ
                                                                        </button>
                                                                </form>
                                                        </div>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>

                                <%-- Nếu không có sản phẩm nào --%>
                                    <c:if test="${empty products}">
                                        <div class="text-center my-5 py-5">
                                            <i class="fas fa-box-open fa-4x text-muted mb-3"></i>
                                            <p class="fs-5 text-secondary">Rất tiếc, không tìm thấy sản phẩm nào phù
                                                hợp!</p>
                                            <a href="/" class="btn btn-primary rounded-pill px-4">Quay lại trang chủ</a>
                                        </div>
                                    </c:if>
                        </div>

                        <jsp:include page="../layout/footer.jsp" />
                        <script
                            src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
                </body>

                </html>