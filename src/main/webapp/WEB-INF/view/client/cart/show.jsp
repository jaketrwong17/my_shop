<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

            <!DOCTYPE html>
            <html lang="vi">

            <head>
                <meta charset="UTF-8">
                <title>Giỏ hàng của bạn - WolfHome</title>
                <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
                <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
                <style>
                    .cart-item {
                        border-radius: 12px;
                        background: #fff;
                        margin-bottom: 15px;
                        padding: 20px;
                        border: 1px solid #eee;
                        transition: 0.3s;
                    }

                    .cart-item:hover {
                        box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
                    }

                    .product-img {
                        width: 100px;
                        height: 100px;
                        object-fit: contain;
                    }

                    .quantity-control {
                        width: 110px;
                        height: 36px;
                    }

                    .quantity-control .btn {
                        padding: 0.2rem 0.6rem;
                        font-size: 0.9rem;
                    }

                    .summary-box {
                        border-radius: 12px;
                        background: #fff;
                        padding: 25px;
                        border: 1px solid #eee;
                        position: sticky;
                        top: 20px;
                    }

                    .btn-checkout {
                        background-color: #4285f4;
                        color: white;
                        font-weight: bold;
                        padding: 12px;
                        border-radius: 8px;
                        width: 100%;
                        border: none;
                        transition: 0.3s;
                        text-transform: uppercase;
                    }

                    .btn-checkout:hover {
                        background-color: #3367d6;
                        color: white;
                    }

                    .gift-box {
                        background-color: #f0f9ff;
                        border: 1px solid #bae6fd;
                        border-radius: 8px;
                        padding: 15px;
                        display: flex;
                        align-items: center;
                        gap: 15px;
                    }

                    .progress-bar-custom {
                        height: 8px;
                        background-color: #28a745;
                        border-radius: 10px;
                    }
                </style>
            </head>

            <body class="bg-light">
                <jsp:include page="../layout/header.jsp" />

                <div class="container mt-4">
                    <nav aria-label="breadcrumb" class="mb-4">
                        <ol class="breadcrumb small">
                            <li class="breadcrumb-item"><a href="/" class="text-decoration-none text-muted">Trang
                                    chủ</a></li>
                            <li class="breadcrumb-item active text-primary">Giỏ hàng</li>
                        </ol>
                    </nav>

                    <h4 class="fw-bold mb-4">Giỏ hàng của bạn</h4>

                    <div class="row g-4">
                        <div class="col-lg-8">
                            <c:if test="${empty cartItems}">
                                <div class="text-center bg-white p-5 rounded shadow-sm border">
                                    <i class="fas fa-shopping-cart fa-3x text-light mb-3"></i>
                                    <p class="text-muted">Giỏ hàng của bạn đang trống.</p>
                                    <a href="/" class="btn btn-primary rounded-pill px-4">Mua sắm ngay</a>
                                </div>
                            </c:if>

                            <c:forEach var="item" items="${cartItems}">
                                <div class="cart-item shadow-sm">
                                    <div class="row align-items-center">
                                        <div class="col-md-2 col-3 text-center">
                                            <img src="/images/${item.product.images[0].imageUrl}" class="product-img">
                                        </div>
                                        <div class="col-md-5 col-9">
                                            <h6 class="fw-bold mb-1">${item.product.name}</h6>
                                            <p class="mb-0 small">Giá: <span class="text-danger fw-bold">
                                                    <fmt:formatNumber value="${item.price}" type="currency"
                                                        currencySymbol="đ" />
                                                </span></p>
                                        </div>
                                        <div class="col-md-4 col-8 mt-md-0 mt-3 text-center">
                                            <div class="d-flex align-items-center justify-content-center gap-3">
                                                <div class="input-group quantity-control border rounded">
                                                    <button class="btn btn-link text-decoration-none text-dark"
                                                        type="button">-</button>
                                                    <input type="text"
                                                        class="form-control border-0 text-center fw-bold bg-white"
                                                        value="${item.quantity}" readonly>
                                                    <button class="btn btn-link text-decoration-none text-dark"
                                                        type="button">+</button>
                                                </div>
                                                <div class="text-end min-vw-100">
                                                    <span class="small text-muted d-block">Tổng:</span>
                                                    <span class="text-danger fw-bold">
                                                        <fmt:formatNumber value="${item.price * item.quantity}"
                                                            type="currency" currencySymbol="đ" />
                                                    </span>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="col-md-1 col-4 text-end mt-md-0 mt-3">
                                            <a href="/delete-cart-item/${item.id}" class="text-muted"><i
                                                    class="far fa-trash-alt h5"></i></a>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>

                            <c:if test="${not empty cartItems}">
                                <div class="gift-box mt-3 shadow-sm border">
                                    <div class="bg-white p-2 rounded border"><i
                                            class="fas fa-gift text-primary fa-2x"></i></div>
                                    <div class="flex-grow-1">
                                        <h6 class="mb-0 fw-bold small text-primary">Quà tặng Viên nước lau sàn Ecovacs
                                            N30 Pro Omni</h6>
                                        <p class="mb-0 text-muted extra-small" style="font-size: 0.75rem;">5 gói</p>
                                        <div class="d-flex justify-content-between mt-1 align-items-center">
                                            <span class="text-muted small italic">Quà tặng</span>
                                            <span class="badge bg-danger text-uppercase p-1"
                                                style="font-size: 0.6rem;">Quà tặng</span>
                                        </div>
                                    </div>
                                </div>
                            </c:if>

                            <div class="mt-4">
                                <textarea class="form-control border-0 shadow-sm rounded-3 p-3" rows="3"
                                    placeholder="Ghi chú đơn hàng (nếu có):"></textarea>
                            </div>
                        </div>

                        <div class="col-lg-4">
                            <div class="summary-box shadow-sm">
                                <h5 class="fw-bold mb-4 text-center border-bottom pb-3">Tóm tắt đơn hàng</h5>
                                <div class="d-flex justify-content-between mb-4">
                                    <span class="text-muted">Tổng tiền sản phẩm:</span>
                                    <span class="text-danger fw-bold h5 mb-0">
                                        <fmt:formatNumber value="${totalPrice}" type="currency" currencySymbol="đ" />
                                    </span>
                                </div>

                                <div class="shipping-info mb-4">
                                    <p class="small text-muted mb-2">Đơn hàng của bạn đã đủ điều kiện miễn phí vận
                                        chuyển!</p>
                                    <div class="progress"
                                        style="height: 8px; border-radius: 10px; background-color: #e9ecef;">
                                        <div class="progress-bar-custom w-100"></div>
                                    </div>
                                </div>

                                <a href="/checkout" class="btn btn-checkout shadow text-decoration-none">Thanh toán đơn
                                    hàng</a>
                                <a href="/" class="btn btn-link w-100 text-decoration-none text-muted small mt-2">Tiếp
                                    tục mua sắm</a>
                            </div>
                        </div>
                    </div>
                </div>

                <jsp:include page="../layout/footer.jsp" />
                <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
            </body>

            </html>