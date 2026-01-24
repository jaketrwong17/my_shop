<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

        <header class="navbar navbar-expand-lg navbar-dark bg-primary py-3 shadow-sm">
            <div class="container">
                <a class="navbar-brand fw-bold fs-3" href="/">wolfhome<span class="text-warning">.</span></a>

                <div class="d-flex flex-grow-1 mx-4">
                    <div class="dropdown me-2">
                        <button class="btn btn-primary border-white dropdown-toggle rounded-pill" type="button"
                            data-bs-toggle="dropdown">
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
                            placeholder="Tìm Robot hút bụi, Nồi cơm...">
                        <button class="btn btn-link position-absolute end-0 top-50 translate-middle-y me-2 text-primary"
                            type="submit">
                            <i class="fas fa-search"></i>
                        </button>
                    </form>
                </div>

                <div class="header-icons d-flex align-items-center gap-3">
                    <a href="/cart" class="text-white text-decoration-none position-relative">
                        <i class="fas fa-shopping-cart fs-4"></i>
                        <%-- QUAN TRỌNG: Badge này sẽ nhảy số ngay khi Hiếu thêm sản phẩm --%>
                            <span
                                class="badge bg-warning text-dark rounded-pill position-absolute top-0 start-100 translate-middle">
                                ${sum != null ? sum : 0}
                            </span>
                    </a>
                </div>
            </div>
        </header>