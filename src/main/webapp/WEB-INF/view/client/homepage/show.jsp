<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

            <!DOCTYPE html>
            <html lang="vi">

            <head>
                <meta charset="UTF-8">
                <title>WolfHome - Cửa hàng</title>
                <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
                <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
                <style>
                    .product-card {
                        transition: all 0.3s ease;
                        border-radius: 12px;
                        overflow: hidden;
                        border: none;
                        height: 100%;
                        display: flex;
                        flex-direction: column;
                        position: relative;
                    }

                    .product-card:hover {
                        transform: translateY(-5px);
                        box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1) !important;
                    }

                    .out-of-stock-label {
                        position: absolute;
                        top: 10px;
                        right: 10px;
                        background: #6c757d;
                        color: white;
                        padding: 2px 10px;
                        border-radius: 4px;
                        font-size: 0.7rem;
                        font-weight: bold;
                        z-index: 10;
                    }

                    .img-container {
                        height: 200px;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        padding: 15px;
                    }

                    .img-container img {
                        max-width: 100%;
                        max-height: 100%;
                        object-fit: contain;
                    }

                    .is-empty {
                        opacity: 0.6;
                        filter: grayscale(50%);
                    }

                    .info-section {
                        background: #fafafa;
                        padding: 15px;
                        flex-grow: 1;
                        text-align: center;
                    }

                    .product-price {
                        color: #ee4d2d;
                        font-weight: bold;
                        font-size: 1.1rem;
                    }

                    .card-footer {
                        background: #fafafa;
                        border: none;
                        padding: 0 15px 15px;
                    }

                    /* Class ẩn sản phẩm */
                    .d-none-custom {
                        display: none !important;
                    }
                </style>
            </head>

            <body class="bg-light">
                <jsp:include page="../layout/header.jsp" />

                <div class="container my-5">
                    <h3 class="fw-bold mb-4" id="product-section-title">DANH SÁCH SẢN PHẨM</h3>

                    <div id="product-list" class="row row-cols-1 row-cols-md-3 row-cols-lg-5 g-4">
                        <c:forEach var="p" items="${products}">
                            <div class="col product-item">
                                <div class="card product-card shadow-sm">
                                    <c:if test="${p.quantity <= 0}">
                                        <div class="out-of-stock-label">Hết hàng</div>
                                    </c:if>

                                    <a href="/product/${p.id}" class="text-decoration-none h-100 d-flex flex-column">
                                        <div class="img-container ${p.quantity <= 0 ? 'is-empty' : ''}">
                                            <img src="/images/${not empty p.images ? p.images[0].imageUrl : 'default.png'}"
                                                alt="${p.name}">
                                        </div>
                                        <div class="info-section flex-grow-1">
                                            <h6 class="text-dark mb-2" style="height: 2.5em; overflow: hidden;">
                                                ${p.name}</h6>
                                            <p class="product-price mb-0">
                                                <fmt:formatNumber value="${p.price}" type="currency"
                                                    currencySymbol="đ" />
                                            </p>
                                        </div>
                                    </a>

                                    <div class="card-footer bg-white">
                                        <a href="/product/${p.id}"
                                            class="btn btn-outline-primary w-100 rounded-pill fw-bold">
                                            Xem ngay
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>

                    <div class="text-center mt-5 mb-4 d-flex gap-2 justify-content-center">
                        <button id="loadMoreBtn" class="btn btn-primary px-4 py-2 rounded-pill fw-bold shadow-sm"
                            style="background-color: #e3f2fd; color: #0d6efd; border: none; display: none;">
                            Xem thêm 20 sản phẩm <i class="fas fa-chevron-down ms-2"></i>
                        </button>

                        <button id="collapseBtn"
                            class="btn btn-outline-secondary px-4 py-2 rounded-pill fw-bold shadow-sm"
                            style="display: none;">
                            Thu gọn <i class="fas fa-chevron-up ms-2"></i>
                        </button>
                    </div>
                </div>

                <jsp:include page="../layout/footer.jsp" />

                <script>
                    document.addEventListener("DOMContentLoaded", function () {
                        const productItems = document.querySelectorAll('.product-item');
                        const loadMoreBtn = document.getElementById('loadMoreBtn');
                        const collapseBtn = document.getElementById('collapseBtn');
                        const titleSection = document.getElementById('product-section-title');

                        // --- CẤU HÌNH ---
                        const initialItems = 20; // Số lượng hiện ban đầu
                        const loadStep = 20;     // Số lượng hiện thêm mỗi lần bấm
                        // ----------------

                        let visibleCount = initialItems;

                        // 1. KHỞI TẠO: Ẩn các sản phẩm vượt quá 20
                        if (productItems.length > initialItems) {
                            loadMoreBtn.style.display = 'inline-block';

                            for (let i = initialItems; i < productItems.length; i++) {
                                productItems[i].classList.add('d-none-custom');
                            }
                            updateBtnText();
                        }

                        // 2. SỰ KIỆN: Bấm nút "Xem thêm"
                        loadMoreBtn.addEventListener('click', function () {
                            const nextCount = visibleCount + loadStep;

                            // Hiện thêm sản phẩm
                            for (let i = visibleCount; i < nextCount; i++) {
                                if (productItems[i]) {
                                    productItems[i].classList.remove('d-none-custom');
                                }
                            }

                            visibleCount = nextCount;
                            updateBtnText();

                            // Hiện nút Thu Gọn (vì đã mở rộng danh sách)
                            collapseBtn.style.display = 'inline-block';

                            // Nếu đã hiện hết -> Ẩn nút Xem Thêm
                            if (visibleCount >= productItems.length) {
                                loadMoreBtn.style.display = 'none';
                            }
                        });

                        // 3. SỰ KIỆN: Bấm nút "Thu gọn"
                        collapseBtn.addEventListener('click', function () {
                            // Ẩn lại tất cả sản phẩm từ vị trí 20 trở đi
                            for (let i = initialItems; i < productItems.length; i++) {
                                productItems[i].classList.add('d-none-custom');
                            }

                            // Reset biến đếm
                            visibleCount = initialItems;

                            // Reset trạng thái nút
                            loadMoreBtn.style.display = 'inline-block';
                            collapseBtn.style.display = 'none';
                            updateBtnText();

                            // Cuộn chuột nhẹ lên tiêu đề danh sách cho người dùng dễ nhìn
                            titleSection.scrollIntoView({ behavior: 'smooth' });
                        });

                        // Hàm cập nhật chữ trên nút Xem thêm
                        function updateBtnText() {
                            const remaining = productItems.length - visibleCount;
                            if (remaining > 0) {
                                const nextShow = remaining < loadStep ? remaining : loadStep;
                                loadMoreBtn.innerHTML = `Xem thêm ${nextShow} sản phẩm <i class="fas fa-chevron-down ms-2"></i>`;
                            }
                        }
                    });
                </script>
            </body>

            </html>