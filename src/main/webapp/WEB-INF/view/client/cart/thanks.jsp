<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

        <!DOCTYPE html>
        <html lang="vi">

        <head>
            <meta charset="UTF-8">
            <title>Đặt hàng thành công - WolfHome</title>
            <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
            <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
        </head>

        <body class="bg-light">

            <jsp:include page="../../layout/header.jsp" />

            <div class="container text-center py-5">
                <div class="card shadow border-0 p-5 mx-auto" style="max-width: 600px; border-radius: 20px;">
                    <div class="text-success mb-4">
                        <i class="fas fa-check-circle fa-5x"></i>
                    </div>

                    <h2 class="fw-bold mb-3">Đặt hàng thành công!</h2>
                    <p class="text-muted mb-4">
                        Cảm ơn bạn đã mua sắm tại WolfHome. <br>
                        Đơn hàng của bạn đang được hệ thống xử lý.
                    </p>

                    <div class="d-flex justify-content-center gap-3">
                        <a href="/" class="btn btn-primary rounded-pill px-4">Tiếp tục mua sắm</a>
                    </div>
                </div>
            </div>

            <jsp:include page="../../layout/footer.jsp" />

        </body>

        </html>