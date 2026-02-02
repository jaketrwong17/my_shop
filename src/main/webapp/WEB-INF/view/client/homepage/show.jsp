<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

            <!DOCTYPE html>
            <html lang="vi">

            <head>
                <meta charset="UTF-8">
                <title>WolfHome - Cửa hàng trực tuyến</title>
                <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
                <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">


                <style>
                    /* ==================== 1. STYLE CHO PRODUCT CARD ==================== */
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

                    .d-none-custom {
                        display: none !important;
                    }

                    /* ==================== 2. STYLE CHO CATEGORY SLIDER ==================== */
                    .category-scroll-container {
                        scroll-behavior: smooth;
                        scrollbar-width: none;
                        /* Firefox */
                        -ms-overflow-style: none;
                        /* IE */
                        padding: 5px;
                    }

                    .category-scroll-container::-webkit-scrollbar {
                        display: none;
                        /* Chrome/Safari */
                    }

                    .scroll-btn {
                        position: absolute;
                        top: 50%;
                        transform: translateY(-50%);
                        z-index: 10;
                        width: 40px;
                        height: 40px;
                        border: 1px solid #dee2e6;
                        opacity: 0.8;
                        transition: 0.3s;
                    }

                    .scroll-btn:hover {
                        opacity: 1;
                        background-color: ##2A83E9;
                        color: white;
                    }

                    .category-card .card {
                        transition: transform 0.2s;
                    }

                    .category-card:hover .card {
                        transform: translateY(-5px);
                        border-color: ##2A83E9 !important;
                        background-color: white !important;
                    }

                    .hover-primary:hover {
                        color: ##2A83E9 !important;
                        text-decoration: underline !important;
                    }

                    /* ==================== 3. STYLE CHO THANH LỌC ==================== */
                    .filter-bar {
                        background-color: #fff;
                        border-radius: 8px;
                        border: 1px solid #dee2e6;
                    }

                    /* STYLE CHO BANNER SLIDER */
                    .carousel-item img {
                        object-fit: cover;
                        /* Đảm bảo ảnh không bị méo */
                        object-position: center;
                    }
                </style>
            </head>

            <body class="bg-light">

                <jsp:include page="../layout/header.jsp" />

                <div class="container-fluid p-0 mb-5">
                    <div id="homeBannerCarousel" class="carousel slide" data-bs-ride="carousel">

                        <div class="carousel-indicators">
                            <button type="button" data-bs-target="#homeBannerCarousel" data-bs-slide-to="0"
                                class="active" aria-current="true" aria-label="Slide 1"></button>
                            <button type="button" data-bs-target="#homeBannerCarousel" data-bs-slide-to="1"
                                aria-label="Slide 2"></button>
                        </div>

                        <div class="carousel-inner">
                            <div class="carousel-item active" data-bs-interval="4000">
                                <img src="https://theme.hstatic.net/200000946105/1001363519/14/collection_banner.jpg?v=1368"
                                    class="d-block w-100" alt="Banner 1"
                                    style="height: 600px; object-fit: cover; object-position: center;">
                            </div>

                            <div class="carousel-item" data-bs-interval="4000">
                                <img src="https://duytan.com/Data/Sites/1/Banner/banner-spm-t12.jpg"
                                    class="d-block w-100" alt="Banner 2"
                                    style="height: 600px; object-fit: cover; object-position: center;">
                            </div>
                        </div>

                        <button class="carousel-control-prev" type="button" data-bs-target="#homeBannerCarousel"
                            data-bs-slide="prev">
                            <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                            <span class="visually-hidden">Previous</span>
                        </button>
                        <button class="carousel-control-next" type="button" data-bs-target="#homeBannerCarousel"
                            data-bs-slide="next">
                            <span class="carousel-control-next-icon" aria-hidden="true"></span>
                            <span class="visually-hidden">Next</span>
                        </button>
                    </div>
                </div>
                <section class="container py-4">
                    <div class="bg-white p-4 rounded-3 shadow-sm position-relative">

                        <div class="d-flex justify-content-between align-items-end mb-3">
                            <h5 class="fw-bold mb-0 text-uppercase border-start border-4 border-primary ps-2">
                                DANH MỤC SẢN PHẨM
                            </h5>
                            <a href="/" class="text-decoration-none text-muted small hover-primary">
                                Tất cả sản phẩm <i class="fas fa-angle-right ms-1"></i>
                            </a>
                        </div>

                        <div class="category-slider-wrapper position-relative">
                            <button class="btn btn-light rounded-circle shadow scroll-btn start-0" id="slideLeft">
                                <i class="fas fa-chevron-left"></i>
                            </button>

                            <div class="d-flex gap-3 overflow-auto category-scroll-container" id="categoryList">
                                <c:forEach var="cat" items="${categories}">
                                    <a href="/?categoryId=${cat.id}"
                                        class="text-decoration-none text-dark category-card">
                                        <div class="card h-100 border border-light bg-light hover-shadow text-center p-3"
                                            style="min-width: 140px;">
                                            <div class="mx-auto mb-2 d-flex align-items-center justify-content-center bg-white rounded-circle shadow-sm"
                                                style="width: 70px; height: 70px;">
                                                <c:choose>
                                                    <c:when test="${not empty cat.image}">
                                                        <img src="/images/${cat.image}" alt="${cat.name}"
                                                            style="width: 60%; height: 60%; object-fit: contain;">
                                                    </c:when>
                                                    <c:otherwise>
                                                        <i class="fas fa-cube fa-lg text-secondary"></i>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                            <div class="card-body p-0">
                                                <span class="fw-bold small d-block text-truncate">${cat.name}</span>
                                            </div>
                                        </div>
                                    </a>
                                </c:forEach>
                            </div>

                            <button class="btn btn-light rounded-circle shadow scroll-btn end-0" id="slideRight">
                                <i class="fas fa-chevron-right"></i>
                            </button>
                        </div>
                    </div>
                </section>

                <div class="container my-5">
                    <h3 class="fw-bold mb-4" id="product-section-title">DANH SÁCH SẢN PHẨM</h3>

                    <form action="/" method="GET" class="mb-4">
                        <c:if test="${not empty param.keyword}">
                            <input type="hidden" name="keyword" value="${param.keyword}">
                        </c:if>
                        <c:if test="${not empty param.categoryId}">
                            <input type="hidden" name="categoryId" value="${param.categoryId}">
                        </c:if>

                        <div
                            class="filter-bar p-3 shadow-sm d-flex flex-wrap align-items-center justify-content-between gap-3">
                            <div class="d-flex align-items-center gap-2">
                                <i class="fas fa-filter text-primary"></i> <span class="fw-bold">Bộ lọc:</span>
                            </div>

                            <div class="d-flex flex-wrap gap-3 align-items-center flex-grow-1 justify-content-end">
                                <div class="d-flex align-items-center gap-2 bg-light p-1 rounded border">
                                    <span class="small text-muted px-2">Giá:</span>
                                    <input type="number" name="priceMin"
                                        class="form-control form-control-sm border-0 bg-white" placeholder="Từ"
                                        value="${param.priceMin}" style="width: 90px;">
                                    <span class="text-muted">-</span>
                                    <input type="number" name="priceMax"
                                        class="form-control form-control-sm border-0 bg-white" placeholder="Đến"
                                        value="${param.priceMax}" style="width: 90px;">
                                    <button type="submit" class="btn btn-primary btn-sm rounded-1"><i
                                            class="fas fa-angle-right"></i></button>
                                </div>

                                <select name="sort" class="form-select form-select-sm border-primary"
                                    style="width: 180px;" onchange="this.form.submit()">
                                    <option value="default" ${param.sort=='default' ? 'selected' : '' }>Mặc định
                                    </option>
                                    <option value="price-asc" ${param.sort=='price-asc' ? 'selected' : '' }>Giá: Thấp
                                        đến Cao</option>
                                    <option value="price-desc" ${param.sort=='price-desc' ? 'selected' : '' }>Giá: Cao
                                        đến Thấp</option>
                                </select>

                                <a href="/" class="btn btn-outline-secondary btn-sm" title="Xóa bộ lọc"><i
                                        class="fas fa-sync-alt"></i></a>
                            </div>
                        </div>
                    </form>

                    <div id="product-list" class="row row-cols-1 row-cols-md-3 row-cols-lg-5 g-4">

                        <c:if test="${empty products}">
                            <div class="col-12 text-center py-5">
                                <div class="py-5">
                                    <i class="fas fa-search fa-3x text-muted mb-3"></i>
                                    <p class="text-muted">Không tìm thấy sản phẩm nào phù hợp!</p>
                                    <a href="/" class="btn btn-primary rounded-pill px-4">Xem tất cả</a>
                                </div>
                            </div>
                        </c:if>

                        <c:forEach var="p" items="${products}">
                            <div class="col product-item">
                                <div class="card product-card shadow-sm h-100">
                                    <c:if test="${p.quantity <= 0}">
                                        <div class="out-of-stock-label">Hết hàng</div>
                                    </c:if>

                                    <a href="/product/${p.id}" class="text-decoration-none h-100 d-flex flex-column">
                                        <div class="img-container ${p.quantity <= 0 ? 'is-empty' : ''}">
                                            <img src="/images/${(not empty p.images and not empty p.images[0]) ? p.images[0].imageUrl : 'default.png'}"
                                                alt="${p.name}"
                                                onerror="this.src='https://placehold.co/200x200?text=No+Image'">
                                        </div>
                                        <div class="info-section flex-grow-1">
                                            <h6 class="text-dark mb-2"
                                                style="height: 2.5em; overflow: hidden; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical;">
                                                ${p.name}
                                            </h6>

                                            <div class="mb-2 small">
                                                <c:if test="${p.reviewCount > 0}">
                                                    <span class="text-warning">
                                                        <c:forEach begin="1" end="${p.averageRating.intValue()}">
                                                            <i class="fas fa-star"></i>
                                                        </c:forEach>
                                                        <c:if test="${p.averageRating % 1 >= 0.5}">
                                                            <i class="fas fa-star-half-alt"></i>
                                                        </c:if>
                                                        <c:forEach begin="1"
                                                            end="${5 - p.averageRating + (p.averageRating % 1 >= 0.5 ? -1 : 0)}">
                                                            <i class="far fa-star text-muted opacity-25"></i>
                                                        </c:forEach>
                                                    </span>
                                                    <span class="text-muted ms-1"
                                                        style="font-size: 0.8rem;">(${p.reviewCount})</span>
                                                </c:if>

                                                <c:if test="${p.reviewCount == 0}">
                                                    <span class="text-muted small" style="font-size: 0.8rem;">Chưa có
                                                        đánh giá</span>
                                                </c:if>
                                            </div>

                                            <p class="product-price mb-0">
                                                <fmt:formatNumber value="${p.price}" type="currency"
                                                    currencySymbol="đ" />
                                            </p>
                                        </div>
                                    </a>

                                    <div class="card-footer bg-white border-0 pb-3">
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
                            style="background-color: #e3f2fd; color: #2A83E9; border: none; display: none;">
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


                        const container = document.getElementById('categoryList');
                        const btnLeft = document.getElementById('slideLeft');
                        const btnRight = document.getElementById('slideRight');

                        if (container && btnLeft && btnRight) {
                            const scrollAmount = 300;
                            btnLeft.addEventListener('click', () => {
                                container.scrollBy({ left: -scrollAmount, behavior: 'smooth' });
                            });
                            btnRight.addEventListener('click', () => {
                                container.scrollBy({ left: scrollAmount, behavior: 'smooth' });
                            });
                        }


                        const productItems = document.querySelectorAll('.product-item');
                        const loadMoreBtn = document.getElementById('loadMoreBtn');
                        const collapseBtn = document.getElementById('collapseBtn');
                        const titleSection = document.getElementById('product-section-title');

                        const initialItems = 20;
                        const loadStep = 20;
                        let visibleCount = initialItems;


                        if (productItems.length > initialItems) {
                            loadMoreBtn.style.display = 'inline-block';
                            for (let i = initialItems; i < productItems.length; i++) {
                                productItems[i].classList.add('d-none-custom');
                            }
                            updateBtnText();
                        }


                        loadMoreBtn.addEventListener('click', function () {
                            const nextCount = visibleCount + loadStep;
                            for (let i = visibleCount; i < nextCount; i++) {
                                if (productItems[i]) productItems[i].classList.remove('d-none-custom');
                            }
                            visibleCount = nextCount;
                            updateBtnText();
                            collapseBtn.style.display = 'inline-block';

                            if (visibleCount >= productItems.length) {
                                loadMoreBtn.style.display = 'none';
                            }
                        });


                        collapseBtn.addEventListener('click', function () {
                            for (let i = initialItems; i < productItems.length; i++) {
                                productItems[i].classList.add('d-none-custom');
                            }
                            visibleCount = initialItems;
                            loadMoreBtn.style.display = 'inline-block';
                            collapseBtn.style.display = 'none';
                            updateBtnText();
                            titleSection.scrollIntoView({ behavior: 'smooth' });
                        });

                        function updateBtnText() {
                            const remaining = productItems.length - visibleCount;
                            if (remaining > 0) {
                                const nextShow = remaining < loadStep ? remaining : loadStep;
                                loadMoreBtn.innerHTML = `Xem thêm \${nextShow} sản phẩm <i class="fas fa-chevron-down ms-2"></i>`;
                            }
                        }
                    });
                </script>
            </body>

            </html>