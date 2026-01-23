<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

        <!DOCTYPE html>
        <html lang="vi">
        <jsp:include page="layout/header.jsp" />

        <body>
            <div class="d-flex" id="wrapper">

                <jsp:include page="layout/sidebar.jsp">
                    <jsp:param name="active" value="dashboard" />
                </jsp:include>

                <div id="page-content-wrapper">

                    <nav class="navbar navbar-expand-lg navbar-light bg-white border-bottom px-4 py-3">
                        <h4 class="mb-0 text-dark fw-bold">Dashboard Thống Kê</h4>
                        <div class="ms-auto">
                            <span class="me-2 text-secondary">Xin chào, Admin</span>
                            <i class="fas fa-user-circle fa-2x text-primary align-middle"></i>
                        </div>
                    </nav>

                    <div class="container-fluid px-4 py-4">

                        <div class="row g-4">
                            <div class="col-xl-3 col-md-6">
                                <div class="card card-stat bg-primary text-white h-100">
                                    <div class="card-body">
                                        <div class="d-flex justify-content-between align-items-center">
                                            <div>
                                                <div class="text-uppercase small fw-bold mb-1">Tổng đơn hàng</div>
                                                <div class="h3 mb-0 fw-bold">150</div>
                                            </div>
                                            <i class="fas fa-shopping-bag fa-3x opacity-50"></i>
                                        </div>
                                        <div class="small mt-3">
                                            <i class="fas fa-arrow-up"></i> 12% so với tháng trước
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="col-xl-3 col-md-6">
                                <div class="card card-stat bg-success text-white h-100">
                                    <div class="card-body">
                                        <div class="d-flex justify-content-between align-items-center">
                                            <div>
                                                <div class="text-uppercase small fw-bold mb-1">Doanh thu</div>
                                                <div class="h3 mb-0 fw-bold">50.000.000 đ</div>
                                            </div>
                                            <i class="fas fa-dollar-sign fa-3x opacity-50"></i>
                                        </div>
                                        <div class="small mt-3">Tháng 1/2026</div>
                                    </div>
                                </div>
                            </div>

                            <div class="col-xl-3 col-md-6">
                                <div class="card card-stat bg-warning text-dark h-100">
                                    <div class="card-body">
                                        <div class="d-flex justify-content-between align-items-center">
                                            <div>
                                                <div class="text-uppercase small fw-bold mb-1">Khách hàng mới</div>
                                                <div class="h3 mb-0 fw-bold">35</div>
                                            </div>
                                            <i class="fas fa-user-plus fa-3x opacity-50"></i>
                                        </div>
                                        <div class="small mt-3 text-dark">
                                            <i class="fas fa-users"></i> Tổng: 1,200 user
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="col-xl-3 col-md-6">
                                <div class="card card-stat bg-danger text-white h-100">
                                    <div class="card-body">
                                        <div class="d-flex justify-content-between align-items-center">
                                            <div>
                                                <div class="text-uppercase small fw-bold mb-1">Hết hàng</div>
                                                <div class="h3 mb-0 fw-bold">5</div>
                                            </div>
                                            <i class="fas fa-exclamation-triangle fa-3x opacity-50"></i>
                                        </div>
                                        <div class="small mt-3">Cần nhập kho gấp</div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="row mt-4">
                            <div class="col-lg-8">
                                <div class="card shadow mb-4">
                                    <div class="card-header py-3">
                                        <h6 class="m-0 font-weight-bold text-primary">Biểu đồ Doanh thu (Demo)</h6>
                                    </div>
                                    <div class="card-body" style="height: 300px; background-color: #fff;">
                                        <p class="text-center text-muted pt-5">Chỗ này sau này tích hợp Chart.js vào nhé
                                        </p>
                                    </div>
                                </div>
                            </div>
                        </div>

                    </div>
                </div>
            </div>

            <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        </body>

        </html>