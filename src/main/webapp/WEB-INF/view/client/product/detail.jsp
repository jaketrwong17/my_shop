<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

            <!DOCTYPE html>
            <html lang="vi">

            <head>
                <meta charset="UTF-8">
                <title>${product.name} - WolfHome</title>
                <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
                <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
                <style>
                    /* Slider & Thumbnails */
                    .carousel-inner img {
                        height: 400px;
                        object-fit: contain;
                        background: #fff;
                    }

                    .thumbnails-wrap {
                        display: flex;
                        gap: 8px;
                        margin-top: 10px;
                        overflow-x: auto;
                        padding: 5px 0;
                    }

                    .thumb-item {
                        width: 60px;
                        height: 60px;
                        border: 1px solid #eee;
                        border-radius: 4px;
                        cursor: pointer;
                        object-fit: cover;
                        transition: 0.2s;
                        flex-shrink: 0;
                    }

                    .thumb-item.active {
                        border-color: #ee4d2d;
                        box-shadow: 0 0 0 1px #ee4d2d;
                    }

                    /* Màu sắc & Nút mua hàng */
                    .color-radio {
                        display: none;
                    }

                    .color-label-small {
                        border: 1px solid #ddd;
                        padding: 4px 12px;
                        cursor: pointer;
                        border-radius: 4px;
                        font-size: 13px;
                        transition: 0.2s;
                        position: relative;
                    }

                    /* Hiệu ứng màu hết hàng */
                    .color-radio:disabled+.color-label-small {
                        background-color: #f5f5f5;
                        color: #ccc;
                        border-style: dashed;
                        cursor: not-allowed;
                        overflow: hidden;
                    }

                    .color-radio:disabled+.color-label-small::after {
                        content: "";
                        position: absolute;
                        top: 50%;
                        left: 0;
                        width: 100%;
                        height: 1px;
                        background: #ccc;
                        transform: rotate(-20deg);
                    }

                    .color-radio:checked+.color-label-small {
                        border-color: #ee4d2d;
                        color: #ee4d2d;
                        background: #fff;
                        font-weight: bold;
                    }

                    .input-group-small {
                        width: 100px !important;
                    }

                    .input-group-small .btn,
                    .input-group-small .form-control {
                        height: 34px;
                        font-size: 0.85rem;
                        padding: 0.2rem 0.4rem;
                    }

                    .btn-shopee-small {
                        background-color: #ee4d2d;
                        color: white;
                        border: none;
                        height: 34px;
                        font-size: 0.85rem;
                        padding: 0 18px;
                        font-weight: bold;
                    }

                    .btn-shopee-small:hover {
                        background-color: #d73211;
                        color: white;
                    }

                    .btn-shopee-small:disabled {
                        background-color: #ccc;
                        cursor: not-allowed;
                    }

                    /* Bảng thông số */
                    .specs-table {
                        border-collapse: collapse;
                        width: 100%;
                        border: 1px solid #dee2e6;
                    }

                    .specs-table td {
                        padding: 10px 12px;
                        font-size: 13px;
                        border: 1px solid #dee2e6 !important;
                        color: #333;
                        font-weight: normal !important;
                    }

                    .specs-table td:first-child {
                        background-color: #f1f3f5;
                        width: 40%;
                    }

                    .detail-content {
                        line-height: 1.7;
                        color: #444;
                        font-size: 0.95rem;
                    }
                </style>
            </head>

            <body class="bg-light">
                <jsp:include page="../layout/header.jsp" />

                <div class="container mt-4">
                    <div class="product-header mb-4 bg-white p-3 rounded shadow-sm border">
                        <nav aria-label="breadcrumb">
                            <ol class="breadcrumb mb-1 small">
                                <li class="breadcrumb-item"><a href="/" class="text-decoration-none text-muted">Trang
                                        chủ</a></li>
                                <li class="breadcrumb-item active text-primary">${product.category.name}</li>
                            </ol>
                        </nav>
                        <div class="d-flex flex-wrap align-items-center justify-content-between gap-3">
                            <h2 class="fw-bold mb-0" style="font-size: 1.4rem;">${product.name}</h2>
                            <div class="rating text-warning small">
                                <i class="fas fa-star"></i><i class="fas fa-star"></i><i class="fas fa-star"></i><i
                                    class="fas fa-star"></i><i class="fas fa-star-half-alt"></i>
                                <span class="text-muted ms-2">(12 đánh giá)</span>
                            </div>
                        </div>
                    </div>

                    <div class="row g-4">
                        <div class="col-lg-5">
                            <div class="bg-white p-3 rounded shadow-sm border h-100 text-center">
                                <div id="productCarousel" class="carousel slide" data-bs-ride="false">
                                    <div class="carousel-inner">
                                        <c:forEach var="img" items="${product.images}" varStatus="status">
                                            <div class="carousel-item ${status.first ? 'active' : ''}">
                                                <img src="/images/${img.imageUrl}" class="d-block w-100">
                                            </div>
                                        </c:forEach>
                                    </div>
                                    <button class="carousel-control-prev" type="button"
                                        data-bs-target="#productCarousel" data-bs-slide="prev">
                                        <span class="carousel-control-prev-icon bg-dark rounded-circle"
                                            aria-hidden="true" style="width: 2rem; height: 2rem;"></span>
                                    </button>
                                    <button class="carousel-control-next" type="button"
                                        data-bs-target="#productCarousel" data-bs-slide="next">
                                        <span class="carousel-control-next-icon bg-dark rounded-circle"
                                            aria-hidden="true" style="width: 2rem; height: 2rem;"></span>
                                    </button>
                                </div>
                                <div class="thumbnails-wrap">
                                    <c:forEach var="img" items="${product.images}" varStatus="status">
                                        <img src="/images/${img.imageUrl}"
                                            class="thumb-item ${status.first ? 'active' : ''}"
                                            data-bs-target="#productCarousel" data-bs-slide-to="${status.index}"
                                            onclick="activateThumb(this)">
                                    </c:forEach>
                                </div>
                            </div>
                        </div>

                        <div class="col-lg-7">
                            <div class="bg-white p-4 rounded shadow-sm border h-100">
                                <div class="price-section mb-3">
                                    <span class="text-muted text-decoration-line-through me-2 small">1.590.000đ</span>
                                    <h2 class="text-danger fw-bold d-inline-block mb-0">
                                        <fmt:formatNumber value="${product.price}" type="currency" currencySymbol="đ" />
                                    </h2>
                                </div>

                                <div class="mb-4 p-3 bg-light rounded border-start border-primary border-4">
                                    <h6 class="fw-bold small text-uppercase mb-2">Đặc điểm nổi bật:</h6>
                                    <div class="small text-secondary">${product.shortDesc}</div>
                                </div>

                                <form action="/add-product-to-cart/${product.id}" method="POST" id="addToCartForm">
                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

                                    <div class="mb-4">
                                        <label class="fw-bold small mb-2 d-block text-uppercase">Màu sắc:
                                            <span id="stock-display" class="text-secondary fw-normal"></span>
                                        </label>
                                        <div class="d-flex gap-2 flex-wrap">
                                            <c:forEach var="color" items="${product.colors}" varStatus="status">
                                                <input type="radio" name="colorId" id="color-${color.id}"
                                                    class="color-radio" data-stock="${color.quantity}"
                                                    value="${color.id}" ${color.quantity <=0 ? 'disabled' : '' }>
                                                <label for="color-${color.id}"
                                                    class="color-label-small">${color.colorName}</label>
                                            </c:forEach>
                                        </div>
                                    </div>

                                    <div class="d-flex gap-2 align-items-center">
                                        <div class="input-group input-group-small">
                                            <button class="btn btn-outline-secondary" type="button"
                                                onclick="changeQty(-1)">-</button>
                                            <input type="number" name="quantity" id="inputQuantity" value="1" min="1"
                                                class="form-control text-center border-secondary">
                                            <button class="btn btn-outline-secondary" type="button"
                                                onclick="changeQty(1)">+</button>
                                        </div>
                                        <button type="submit" id="btnSubmit"
                                            class="btn btn-shopee-small rounded-pill text-uppercase">
                                            <i class="fas fa-cart-plus me-2"></i>Thêm vào giỏ hàng
                                        </button>
                                    </div>
                                    <div id="error-msg" class="text-danger small mt-2" style="display:none;">Số lượng
                                        vượt quá kho hàng!</div>
                                </form>

                                <div class="mt-4 pt-3 border-top d-flex gap-4 small text-muted">
                                    <span><i class="fas fa-undo-alt text-primary me-1"></i> 30 ngày đổi trả</span>
                                    <span><i class="fas fa-shield-alt text-primary me-1"></i> Chính hãng 100%</span>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="row mt-4 g-4">
                        <div class="col-lg-8">
                            <div class="bg-white p-4 rounded shadow-sm border mb-4">
                                <h5 class="fw-bold border-bottom pb-3 mb-3 text-uppercase"><i
                                        class="fas fa-info-circle text-primary me-2"></i>Thông tin sản phẩm</h5>
                                <div class="detail-content">${product.detailDesc}</div>
                            </div>
                        </div>
                        <div class="col-lg-4">
                            <div class="specs-box p-3 shadow-sm border bg-white">
                                <h6 class="fw-bold mb-3 text-primary text-uppercase small"><i
                                        class="fas fa-list-ul me-2"></i>Cấu hình chi tiết</h6>
                                <table class="specs-table">
                                    <tbody>
                                        <c:forEach var="s" items="${product.specs}">
                                            <tr>
                                                <td>${s.specName}</td>
                                                <td>${s.specValue}</td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>

                <jsp:include page="../layout/footer.jsp" />

                <script>
                    const inputQty = document.getElementById('inputQuantity');
                    const btnSubmit = document.getElementById('btnSubmit');
                    const stockDisplay = document.getElementById('stock-display');
                    const errorMsg = document.getElementById('error-msg');
                    const colorRadios = document.querySelectorAll('.color-radio');
                    let currentStock = 0;

                    function activateThumb(element) {
                        document.querySelectorAll('.thumb-item').forEach(t => t.classList.remove('active'));
                        element.classList.add('active');
                    }

                    colorRadios.forEach(radio => {
                        radio.addEventListener('change', function () {
                            currentStock = parseInt(this.getAttribute('data-stock'));
                            stockDisplay.innerText = `(Còn lại: \${currentStock} sản phẩm)`;
                            inputQty.value = 1;
                            validateQuantity();
                        });
                    });

                    function changeQty(amt) {
                        inputQty.value = Math.max(1, parseInt(inputQty.value) + amt);
                        validateQuantity();
                    }

                    function validateQuantity() {
                        let val = parseInt(inputQty.value);
                        if (val > currentStock) {
                            inputQty.value = currentStock;
                            errorMsg.style.display = 'block';
                            setTimeout(() => { errorMsg.style.display = 'none'; }, 2000);
                        }
                        btnSubmit.disabled = (currentStock <= 0);
                    }

                    inputQty.addEventListener('change', validateQuantity);

                    window.addEventListener('DOMContentLoaded', () => {
                        const firstAvailable = document.querySelector('.color-radio:not(:disabled)');
                        if (firstAvailable) { firstAvailable.click(); }
                        else { stockDisplay.innerText = "(Hết hàng)"; btnSubmit.disabled = true; inputQty.disabled = true; }
                    });
                </script>
            </body>

            </html>