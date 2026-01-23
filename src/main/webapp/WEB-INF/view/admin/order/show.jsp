<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

            <!DOCTYPE html>
            <html lang="vi">
            <jsp:include page="../layout/header.jsp" />

            <body>
                <div class="d-flex" id="wrapper">
                    <jsp:include page="../layout/sidebar.jsp">
                        <jsp:param name="active" value="order" />
                    </jsp:include>

                    <div id="page-content-wrapper">
                        <nav class="navbar navbar-expand-lg navbar-light bg-white border-bottom px-4 py-3">
                            <h4 class="mb-0 text-dark fw-bold">Quản lý Đơn hàng</h4>
                        </nav>

                        <div class="container-fluid px-4 py-4">
                            <div class="card shadow-sm border-0 rounded-3">
                                <div class="card-header bg-white py-3">
                                    <div class="row">
                                        <div class="col-md-6">
                                            <form class="d-flex">
                                                <input class="form-control me-2" type="search"
                                                    placeholder="Tìm ID đơn hàng...">
                                                <button class="btn btn-outline-primary" type="submit">Tìm</button>
                                            </form>
                                        </div>
                                    </div>
                                </div>

                                <div class="card-body p-0">
                                    <table class="table table-hover align-middle mb-0">
                                        <thead class="bg-light text-secondary">
                                            <tr>
                                                <th class="ps-4">ID</th>
                                                <th>Người nhận</th>
                                                <th>Tổng tiền</th>
                                                <th>Trạng thái</th>
                                                <th>Hành động</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="order" items="${orders}">
                                                <tr>
                                                    <td class="ps-4 fw-bold">#${order.id}</td>
                                                    <td>
                                                        <div class="fw-bold">${order.receiverName}</div>
                                                        <small class="text-muted">${order.receiverPhone}</small>
                                                    </td>
                                                    <td class="text-danger fw-bold">
                                                        <fmt:formatNumber value="${order.totalPrice}" type="currency"
                                                            currencySymbol="đ" />
                                                    </td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${order.status == 'PENDING'}">
                                                                <span class="badge bg-warning text-dark">Chờ xác
                                                                    nhận</span>
                                                            </c:when>
                                                            <c:when test="${order.status == 'SHIPPING'}">
                                                                <span class="badge bg-info text-dark">Đang giao</span>
                                                            </c:when>
                                                            <c:when test="${order.status == 'COMPLETED'}">
                                                                <span class="badge bg-success">Hoàn thành</span>
                                                            </c:when>
                                                            <c:when test="${order.status == 'CANCELLED'}">
                                                                <span class="badge bg-danger">Đã hủy</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="badge bg-secondary">${order.status}</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>
                                                        <a href="/admin/order/view/${order.id}"
                                                            class="btn btn-sm btn-primary">
                                                            <i class="fas fa-eye"></i> Xem & Xử lý
                                                        </a>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
            </body>

            </html>