<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

            <!DOCTYPE html>
            <html lang="vi">

            <head>
                <title>${product.name}</title>
                <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
            </head>

            <body>
                <jsp:include page="../layout/header.jsp" />

                <div class="container my-5 bg-white p-4 rounded shadow-sm">
                    <div class="row">
                        <div class="col-md-5">
                            <img src="/images/${product.images[0].imageUrl}" class="img-fluid rounded border"
                                alt="${product.name}">
                        </div>
                        <div class="col-md-7">
                            <h2 class="fw-bold">${product.name}</h2>
                            <h3 class="text-danger fw-bold my-3">
                                <fmt:formatNumber value="${product.price}" type="currency" currencySymbol="đ" />
                            </h3>
                            <p><strong>Hãng sản xuất:</strong> ${product.factory}</p>
                            <p><strong>Mô tả:</strong> ${product.shortDesc}</p>

                            <hr>

                            <form action="/add-to-cart" method="POST">
                                <input type="hidden" name="productId" value="${product.id}">
                                <div class="d-flex gap-3 align-items-center">
                                    <input type="number" name="quantity" value="1" min="1" class="form-control w-25">
                                    <button type="submit" class="btn btn-primary btn-lg px-4">Thêm vào giỏ hàng</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </body>

            </html>