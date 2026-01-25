<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

            <!DOCTYPE html>
            <html lang="vi">

            <head>
                <meta charset="UTF-8">
                <title>Thông tin tài khoản - WolfHome</title>
                <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
                <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
            </head>

            <body class="bg-light">
                <jsp:include page="../layout/header.jsp" />

                <div class="container my-5">
                    <div class="row">
                        <div class="col-md-3 mb-4">
                            <div class="card border-0 shadow-sm rounded-3">
                                <div class="card-body text-center py-4">
                                    <div class="bg-warning rounded-circle d-flex justify-content-center align-items-center mx-auto mb-3 fw-bold fs-2"
                                        style="width: 80px; height: 80px;">
                                        ${user.fullName.charAt(0)}
                                    </div>
                                    <h5 class="fw-bold">${user.fullName}</h5>
                                    <p class="text-muted small">${user.email}</p>
                                </div>
                                <div class="list-group list-group-flush small">
                                    <a href="/profile"
                                        class="list-group-item list-group-item-action active border-0 fw-bold">
                                        <i class="fas fa-user-circle me-2"></i> Thông tin tài khoản
                                    </a>
                                    <a href="/history" class="list-group-item list-group-item-action border-0">
                                        <i class="fas fa-history me-2"></i> Lịch sử mua hàng
                                    </a>
                                    <a href="/change-password" class="list-group-item list-group-item-action border-0">
                                        <i class="fas fa-key me-2"></i> Đổi mật khẩu
                                    </a>
                                    <form action="/logout" method="post"
                                        class="list-group-item list-group-item-action border-0 text-danger">
                                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                        <button type="submit"
                                            class="btn btn-link text-danger text-decoration-none p-0 w-100 text-start">
                                            <i class="fas fa-sign-out-alt me-2"></i> Đăng xuất
                                        </button>
                                    </form>
                                </div>
                            </div>
                        </div>

                        <div class="col-md-9">
                            <div class="card border-0 shadow-sm rounded-3">
                                <div class="card-header bg-white border-bottom py-3">
                                    <h5 class="mb-0 fw-bold">Hồ sơ của tôi</h5>
                                    <small class="text-muted">Quản lý thông tin hồ sơ để bảo mật tài khoản</small>
                                </div>
                                <div class="card-body p-4">
                                    <form:form action="/profile" method="post" modelAttribute="user">

                                        <div class="row mb-3 align-items-center">
                                            <label class="col-md-3 col-form-label text-md-end text-muted">Email</label>
                                            <div class="col-md-7">
                                                <div class="input-group">
                                                    <span class="input-group-text bg-light"><i
                                                            class="fas fa-envelope text-muted"></i></span>
                                                    <form:input path="email" class="form-control bg-light"
                                                        readonly="true" />
                                                </div>
                                                <small class="text-muted fst-italic mt-1 d-block">* Email không thể thay
                                                    đổi</small>
                                            </div>
                                        </div>

                                        <div class="row mb-3 align-items-center">
                                            <label class="col-md-3 col-form-label text-md-end text-muted">Họ và
                                                tên</label>
                                            <div class="col-md-7">
                                                <form:input path="fullName" class="form-control" />
                                            </div>
                                        </div>

                                        <div class="row mb-3 align-items-center">
                                            <label class="col-md-3 col-form-label text-md-end text-muted">Số điện
                                                thoại</label>
                                            <div class="col-md-7">
                                                <form:input path="phone" class="form-control"
                                                    placeholder="Thêm số điện thoại" />
                                            </div>
                                        </div>

                                        <div class="row mb-3 align-items-center">
                                            <label class="col-md-3 col-form-label text-md-end text-muted">Địa
                                                chỉ</label>
                                            <div class="col-md-7">
                                                <form:input path="address" class="form-control"
                                                    placeholder="Thêm địa chỉ nhận hàng" />
                                            </div>
                                        </div>

                                        <div class="row mt-4">
                                            <div class="col-md-7 offset-md-3">
                                                <button type="submit"
                                                    class="btn btn-primary px-4 rounded-pill fw-bold">Lưu thay
                                                    đổi</button>
                                            </div>
                                        </div>
                                    </form:form>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <jsp:include page="../layout/footer.jsp" />
                <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
            </body>

            </html>