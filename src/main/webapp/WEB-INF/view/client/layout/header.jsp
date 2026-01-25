<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

        <header class="navbar navbar-expand-lg navbar-dark bg-primary py-3 shadow-sm sticky-top">
            <div class="container">
                <a class="navbar-brand fw-bold fs-3" href="/">wolfhome<span class="text-warning">.</span></a>

                <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarContent">
                    <span class="navbar-toggler-icon"></span>
                </button>

                <div class="collapse navbar-collapse" id="navbarContent">
                    <div class="d-flex flex-grow-1 mx-lg-4 my-2 my-lg-0">
                        <div class="dropdown me-2">
                            <button class="btn btn-primary border-white dropdown-toggle rounded-pill w-100"
                                type="button" data-bs-toggle="dropdown">
                                <i class="fas fa-bars me-2"></i>Danh mục
                            </button>
                            <ul class="dropdown-menu shadow border-0">
                                <c:forEach var="cat" items="${categories}">
                                    <li><a class="dropdown-item" href="/?categoryId=${cat.id}">${cat.name}</a></li>
                                </c:forEach>
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

                        <c:if test="${empty sessionScope.email}">
                            <a href="/login" class="btn btn-outline-light rounded-pill fw-bold btn-sm px-3">Đăng
                                nhập</a>
                            <a href="/register" class="btn btn-light text-primary rounded-pill fw-bold btn-sm px-3">Đăng
                                ký</a>
                        </c:if>

                        <c:if test="${not empty sessionScope.email}">
                            <div class="dropdown">
                                <button
                                    class="btn btn-link text-white text-decoration-none dropdown-toggle d-flex align-items-center"
                                    type="button" id="userDropdown" data-bs-toggle="dropdown" aria-expanded="false">
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
                                    <li>
                                        <a class="dropdown-item py-2" href="/profile">
                                            <i class="fas fa-id-card me-2 text-primary"></i> Thông tin tài khoản
                                        </a>
                                    </li>
                                    <li>
                                        <a class="dropdown-item py-2" href="/history">
                                            <i class="fas fa-box-open me-2 text-success"></i> Đơn hàng của tôi
                                        </a>
                                    </li>

                                    <%-- <sec:authorize access="hasRole('ADMIN')">
                                        <li>
                                            <hr class="dropdown-divider">
                                        </li>
                                        <li><a class="dropdown-item py-2" href="/admin"><i
                                                    class="fas fa-user-shield me-2 text-danger"></i> Trang quản trị</a>
                                        </li>
                                        </sec:authorize>
                                        --%>

                                        <li>
                                            <hr class="dropdown-divider">
                                        </li>

                                        <li>
                                            <form action="/logout" method="post" class="m-0">
                                                <input type="hidden" name="${_csrf.parameterName}"
                                                    value="${_csrf.token}" />
                                                <button type="submit" class="dropdown-item py-2 text-danger fw-bold">
                                                    <i class="fas fa-sign-out-alt me-2"></i> Đăng xuất
                                                </button>
                                            </form>
                                        </li>
                                </ul>
                            </div>
                        </c:if>

                    </div>
                </div>
            </div>
        </header>