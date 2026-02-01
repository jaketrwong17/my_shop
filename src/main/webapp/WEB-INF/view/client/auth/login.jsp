<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

        <!DOCTYPE html>
        <html lang="vi">

        <head>
            <title>Đăng nhập - WolfHome</title>
            <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        </head>

        <body class="bg-light">
            <div class="container d-flex justify-content-center align-items-center vh-100">
                <div class="card shadow p-4" style="width: 400px; border-radius: 15px;">
                    <h3 class="text-center mb-4 fw-bold text-primary">Đăng nhập</h3>

                    <c:if test="${param.error != null}">
                        <div class="alert alert-danger text-center">
                            Email hoặc mật khẩu không đúng!
                        </div>
                    </c:if>

                    <form action="/login" method="post">
                        <div class="mb-3">
                            <label class="form-label">Email</label>
                            <input type="email" name="username" class="form-control" required
                                placeholder="name@example.com">
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Mật khẩu</label>
                            <input type="password" name="password" class="form-control" required>
                        </div>
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

                        <button type="submit" class="btn btn-primary w-100 rounded-pill">Đăng nhập</button>
                    </form>

                    <div class="text-center mt-3">
                        <small>Chưa có tài khoản? <a href="/register" class="text-decoration-none">Đăng ký
                                ngay</a></small>
                    </div>
                    <div class="text-center mt-2">
                        <small><a href="/" class="text-decoration-none text-muted">Quay lại trang chủ</a></small>
                    </div>
                </div>
            </div>
            <script>
                document.addEventListener("DOMContentLoaded", function () {


                    document.querySelector('input[name="password"]').value = "123456";
                });
            </script>
        </body>

        </html>