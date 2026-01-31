<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

            <style>
                /* Container chứa dòng chạy */
                .coupon-ticker-wrap {
                    background-color: #0D6EFD;
                    /* Vàng nhạt */
                    border-bottom: 1px solid #ffecb5;
                    overflow: hidden;
                    white-space: nowrap;
                    height: 40px;
                    display: flex;
                    align-items: center;
                    position: relative;
                    z-index: 1;
                    /* Đảm bảo nằm dưới Navbar nhưng trên nội dung khác */
                }

                /* Phần nội dung chạy */
                .coupon-ticker-content {
                    display: inline-block;
                    padding-left: 100%;

                    animation: ticker-scroll 300s linear infinite;
                }

                @keyframes ticker-scroll {
                    0% {
                        transform: translate3d(0, 0, 0);
                    }

                    100% {
                        transform: translate3d(-100%, 0, 0);
                    }
                }

                /* Di chuột vào thì dừng lại
                .coupon-ticker-content:hover {
                    animation-play-state: paused;
                    cursor: pointer;
                } */

                /* Style cho từng mã */
                .coupon-item {
                    margin-right: 60px;
                    font-weight: 500;
                    color: #fff;
                    font-size: 0.9rem;
                }

                .coupon-code {
                    background-color: #dc3545;
                    color: white;
                    padding: 2px 8px;
                    border-radius: 4px;
                    font-weight: bold;
                    border: 1px dashed white;
                    margin-left: 5px;
                }
            </style>

            <header class="navbar navbar-expand-lg navbar-dark bg-primary py-3  sticky-top">
                <div class="container">
                    <a class="navbar-brand fw-bold fs-3" href="/">16Home<span class="text-warning">.</span></a>

                    <button class="navbar-toggler" type="button" data-bs-toggle="collapse"
                        data-bs-target="#navbarContent">
                        <span class="navbar-toggler-icon"></span>
                    </button>

                    <div class="collapse navbar-collapse" id="navbarContent">
                        <div class="d-flex flex-grow-1 mx-lg-4 my-2 my-lg-0">
                            <div class="dropdown me-2">
                                <button class="btn btn-primary border-white dropdown-toggle rounded-pill w-100"
                                    type="button" id="dropdownMenuCategory" data-bs-toggle="dropdown"
                                    aria-expanded="false">
                                    <i class="fas fa-bars me-2"></i>Danh mục
                                </button>
                                <ul class="dropdown-menu shadow border-0" aria-labelledby="dropdownMenuCategory">
                                    <c:choose>
                                        <c:when test="${not empty categories}">
                                            <c:forEach var="cat" items="${categories}">
                                                <li><a class="dropdown-item"
                                                        href="/?categoryId=${cat.id}">${cat.name}</a></li>
                                            </c:forEach>
                                        </c:when>
                                        <c:otherwise>
                                            <li><a class="dropdown-item disabled" href="#">Chưa có danh mục</a></li>
                                        </c:otherwise>
                                    </c:choose>
                                </ul>
                            </div>

                            <form class="flex-grow-1 d-flex position-relative" action="/" method="GET">
                                <input class="form-control rounded-pill pe-5" type="search" name="search"
                                    placeholder="Tìm Robot hút bụi, Nồi cơm..." value="${param.search}">
                                <button
                                    class="btn btn-link position-absolute end-0 top-50 translate-middle-y me-2 text-primary"
                                    type="submit">
                                    <i class="fas fa-search"></i>
                                </button>
                            </form>
                        </div>

                        <div class="d-flex align-items-center gap-3 justify-content-end">
                            <a href="/cart" class="text-white text-decoration-none position-relative me-2">
                                <i class="fas fa-shopping-cart fs-4"></i>
                                <span
                                    class="badge bg-warning text-dark rounded-pill position-absolute top-0 start-100 translate-middle">
                                    ${sessionScope.sum != null ? sessionScope.sum : 0}
                                </span>
                            </a>

                            <c:choose>
                                <c:when test="${empty sessionScope.email}">
                                    <a href="/login" class="btn btn-outline-light rounded-pill fw-bold btn-sm px-3">Đăng
                                        nhập</a>
                                    <a href="/register"
                                        class="btn btn-light text-primary rounded-pill fw-bold btn-sm px-3">Đăng ký</a>
                                </c:when>
                                <c:otherwise>
                                    <div class="dropdown">
                                        <button
                                            class="btn btn-link text-white text-decoration-none dropdown-toggle d-flex align-items-center"
                                            type="button" id="userDropdown" data-bs-toggle="dropdown"
                                            aria-expanded="false">
                                            <div class="bg-warning rounded-circle d-flex justify-content-center align-items-center text-dark fw-bold me-2"
                                                style="width: 35px; height: 35px;">
                                                <i class="fas fa-user"></i>
                                            </div>
                                            <span class="d-none d-md-inline-block fw-bold small text-truncate"
                                                style="max-width: 150px;">
                                                ${sessionScope.email}
                                            </span>
                                        </button>
                                        <ul class="dropdown-menu dropdown-menu-end shadow border-0 mt-2"
                                            aria-labelledby="userDropdown">
                                            <c:if test="${sessionScope.role == 'ADMIN'}">
                                                <li>
                                                    <a class="dropdown-item py-2" href="/admin/user">
                                                        <i class="fas fa-user-shield me-2 text-danger"></i> Trang quản
                                                        trị
                                                    </a>
                                                </li>
                                                <li>
                                                    <hr class="dropdown-divider">
                                                </li>
                                            </c:if>
                                            <li><a class="dropdown-item py-2" href="/profile"><i
                                                        class="fas fa-id-card me-2 text-primary"></i> Thông tin tài
                                                    khoản</a></li>
                                            <li><a class="dropdown-item py-2" href="/history"><i
                                                        class="fas fa-box-open me-2 text-success"></i> Đơn hàng của
                                                    tôi</a></li>

                                            <li>
                                                <hr class="dropdown-divider">
                                            </li>
                                            <li>
                                                <form action="/logout" method="post" class="m-0">
                                                    <c:if test="${not empty _csrf}">
                                                        <input type="hidden" name="${_csrf.parameterName}"
                                                            value="${_csrf.token}" />
                                                    </c:if>
                                                    <button type="submit"
                                                        class="dropdown-item py-2 text-danger fw-bold">
                                                        <i class="fas fa-sign-out-alt me-2"></i> Đăng xuất
                                                    </button>
                                                </form>
                                            </li>
                                        </ul>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </header>

            </header>

            <div class="coupon-ticker-wrap py-2">
                <div class="coupon-ticker-content">
                    <c:forEach begin="1" end="20">
                        <span class="coupon-item">
                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 16 16"
                                fill="none">
                                <path stroke="#fff" stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
                                    d="M4.5 6a4 4 0 1 0 8 0 4 4 0 0 0-8 0Z"></path>
                                <path stroke="#fff" stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
                                    d="m8.5 10 2.267 3.927 1.065-2.156 2.399.155L11.964 8M5.035 8l-2.267 3.927 2.399-.156 1.065 2.155L8.499 10">
                                </path>
                            </svg>
                            Sản phẩm chính hãng
                        </span>

                        <span class="coupon-item">
                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24"
                                fill="none" stroke="#fff" stroke-width="2" stroke-linecap="round"
                                stroke-linejoin="round">
                                <path
                                    d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z">
                                </path>
                            </svg>
                            Tổng đài tư vấn: 0968 733 752
                        </span>

                        <span class="coupon-item">
                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="none"
                                viewBox="0 0 17 16">
                                <path stroke="#fff" stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
                                    d="M3.833 11.333a1.333 1.333 0 1 0 2.667 0 1.333 1.333 0 0 0-2.667 0ZM10.5 11.333a1.333 1.333 0 1 0 2.667 0 1.333 1.333 0 0 0-2.667 0Z">
                                </path>
                                <path stroke="#fff" stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
                                    d="M3.833 11.333H2.5V8.667m-.667-5.334h7.334v8m-2.667 0h4m2.667 0H14.5v-4m0 0H9.167m5.333 0L12.5 4H9.167M2.5 6h2.667">
                                </path>
                            </svg>
                            Giao hàng tận nơi
                        </span>

                        <span class="coupon-item">
                            <svg xmlns="http://www.w3.org/2000/svg" width="17" height="16" viewBox="0 0 17 16"
                                fill="none">
                                <path stroke="#fff" stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
                                    d="M3.167 8V6a2 2 0 0 1 2-2h8.666m0 0-2-2m2 2-2 2M13.833 8v2a2 2 0 0 1-2 2H3.167m0 0 2 2m-2-2 2-2">
                                </path>
                            </svg>
                            Đổi trả miễn phí trong vòng 7 ngày
                        </span>
                    </c:forEach>
                </div>
            </div>