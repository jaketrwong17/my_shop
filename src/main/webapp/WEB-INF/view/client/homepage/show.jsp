<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

            <!DOCTYPE html>
            <html lang="vi">

            <head>
                <meta charset="UTF-8">
                <title>WolfHome - Robot Hút Bụi Thông Minh</title>
                <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
                <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
                <style>
                    /* Card tổng thể: Bo góc rộng và đổ bóng cực mịn */
                    .product-card {
                        transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
                        border: none !important;
                        border-radius: 15px;
                        overflow: hidden;
                        background-color: #fff;
                        display: flex;
                        flex-direction: column;
                        height: 100%;
                    }

                    /* Hiệu ứng nhấc nhẹ khi di chuột vào */
                    .product-card:hover {
                        transform: translateY(-8px);
                        box-shadow: 0 12px 24px rgba(0, 0, 0, 0.08) !important;
                    }

                    /* Vùng chứa ảnh: Cố định 210px để form luôn đều */
                    .product-img-container {
                        height: 210px;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        background-color: #ffffff;
                        padding: 20px;
                    }

                    .product-img-container img {
                        max-width: 100%;
                        max-height: 100%;
                        object-fit: contain;
                        /* Đảm bảo ảnh không bị méo */
                    }

                    /* Vùng thông tin: Màu trắng xám cực nhạt (#fafafa), không gạch ngang */
                    .product-info-section {
                        background-color: #fafafa;
                        padding: 15px 20px;
                        flex-grow: 1;
                        /* Giúp các card cao bằng nhau */
                        border: none !important;
                    }

                    /* Giới hạn tên sản phẩm tối đa 2 dòng */
                    .product-title {
                        height: 2.4em;
                        line-height: 1.2em;
                        overflow: hidden;
                        display: -webkit-box;
                        -webkit-line-clamp: 2;
                        -webkit-box-orient: vertical;
                        font-size: 0.9rem;
                        color: #333;
                        margin-bottom: 5px;
                    }

                    /* Màu sao đánh giá */
                    .product-rating {
                        color: #ffc107;
                        font-size: 0.8rem;
                        margin-bottom: 10px;
                    }

                    /* Giá tiền đỏ cam nổi bật */
                    .product-price {
                        color: #ee4d2d;
                        font-weight: 700;
                        font-size: 1.1rem;
                        margin-bottom: 5px;
                    }

                    /* Footer chứa nút bấm gộp màu với vùng info */
                    .card-footer-custom {
                        background-color: #fafafa;
                        border: none !important;
                        padding: 0 20px 20px 20px;
                    }

                    .btn-add-cart {
                        font-size: 0.85rem;
                        font-weight: 600;
                        padding: 10px 0;
                        transition: all 0.2s;
                    }

                    .btn-add-cart:hover {
                        filter: brightness(1.1);
                        transform: scale(1.02);
                    }
                </style>
            </head>

            <body class="bg-light">
                <jsp:include page="../layout/header.jsp" />

                <div class="container my-5">
                    <h3 class="fw-bold text-uppercase border-start border-primary border-4 ps-3 mb-4">
                        Sản phẩm mới nhất
                    </h3>

                    <div class="row row-cols-1 row-cols-sm-2 row-cols-md-3 row-cols-lg-5 g-4">
                        <c:forEach var="p" items="${products}">
                            <div class="col">
                                <div class="card product-card shadow-sm">
                                    <a href="/product/${p.id}" class="text-decoration-none d-flex flex-column h-100">
                                        <div class="product-img-container">
                                            <c:if test="${not empty p.images}">
                                                <img src="/images/${p.images[0].imageUrl}" alt="${p.name}">
                                            </c:if>
                                            <c:if test="${empty p.images}">
                                                <img src="/images/default-product.png" alt="No image">
                                            </c:if>
                                        </div>

                                        <div class="product-info-section text-center">
                                            <h6 class="product-title fw-bold">${p.name}</h6>

                                            <div class="product-rating">
                                                <i class="fas fa-star"></i>
                                                <i class="fas fa-star"></i>
                                                <i class="fas fa-star"></i>
                                                <i class="fas fa-star"></i>
                                                <i class="fas fa-star"></i>
                                            </div>

                                            <p class="product-price mb-0">
                                                <fmt:formatNumber value="${p.price}" type="currency"
                                                    currencySymbol="đ" />
                                            </p>
                                        </div>
                                    </a>

                                    <div class="card-footer-custom">
                                        <form action="/add-product-to-cart/${p.id}" method="POST">
                                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

                                            <input type="hidden" name="quantity" value="1">
                                            <button type="submit"
                                                class="btn btn-primary w-100 rounded-pill btn-add-cart">
                                                <i class="fas fa-shopping-cart me-2"></i>Thêm vào giỏ
                                            </button>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </div>

                <jsp:include page="../layout/footer.jsp" />
                <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
            </body>

            </html>