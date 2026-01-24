<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

            <!DOCTYPE html>
            <html lang="vi">

            <head>
                <title>Đăng ký - WolfHome</title>
                <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
            </head>

            <body class="bg-light">
                <div class="container d-flex justify-content-center align-items-center vh-100">
                    <div class="card shadow p-4" style="width: 500px; border-radius: 15px;">
                        <h3 class="text-center mb-4 fw-bold text-success">Đăng ký tài khoản</h3>

                        <c:if test="${not empty error}">
                            <div class="alert alert-danger text-center" role="alert">
                                ${error}
                            </div>
                        </c:if>

                        <form:form action="/register" method="post" modelAttribute="registerUser">
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Họ</label>
                                    <form:input path="firstName" class="form-control" required="true" />
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Tên</label>
                                    <form:input path="lastName" class="form-control" required="true" />
                                </div>
                            </div>

                            <div class="mb-3">
                                <label class="form-label">Email</label>
                                <form:input path="email" type="email" class="form-control" required="true" />
                            </div>

                            <div class="mb-3">
                                <label class="form-label">Mật khẩu</label>
                                <form:input path="password" type="password" class="form-control" required="true" />
                            </div>

                            <div class="mb-3">
                                <label class="form-label">Xác nhận mật khẩu</label>
                                <form:input path="confirmPassword" type="password" class="form-control"
                                    required="true" />
                            </div>

                            <button type="submit" class="btn btn-success w-100 rounded-pill">Đăng ký</button>
                        </form:form>

                        <div class="text-center mt-3">
                            <small>Đã có tài khoản? <a href="/login" class="text-decoration-none">Đăng nhập</a></small>
                        </div>

                        <div class="text-center mt-2">
                            <small><a href="/" class="text-decoration-none text-muted">Quay lại trang chủ</a></small>
                        </div>
                    </div>
                </div>
            </body>

            </html>