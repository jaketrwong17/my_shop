<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

        <!DOCTYPE html>
        <html lang="vi">
        <jsp:include page="../layout/header.jsp" />

        <body>
            <div class="d-flex" id="wrapper">
                <jsp:include page="../layout/sidebar.jsp">
                    <jsp:param name="active" value="category" />
                </jsp:include>

                <div id="page-content-wrapper">
                    <nav class="navbar navbar-expand-lg navbar-light bg-white border-bottom px-4 py-3">
                        <h4 class="mb-0 text-dark fw-bold">Quản lý Danh mục</h4>
                    </nav>

                    <div class="container-fluid px-4 py-4">
                        <div class="card shadow-sm border-0 rounded-3">
                            <div class="card-header bg-white py-3">
                                <div class="row align-items-center">
                                    <div class="col-md-6">
                                        <form action="/admin/category" method="GET" class="d-flex">
                                            <input type="text" name="keyword" class="form-control"
                                                placeholder="Tìm danh mục..." value="${keyword}">
                                            <button class="btn btn-outline-primary ms-2"><i
                                                    class="fas fa-search"></i></button>
                                        </form>
                                    </div>
                                    <div class="col-md-6 text-end">
                                        <a href="/admin/category/create" class="btn btn-primary">
                                            <i class="fas fa-plus me-2"></i> Thêm danh mục
                                        </a>
                                    </div>
                                </div>
                            </div>

                            <div class="card-body p-0">
                                <table class="table table-hover align-middle mb-0">
                                    <thead class="bg-light text-secondary">
                                        <tr>
                                            <th class="ps-4">ID</th>
                                            <th>Tên danh mục</th>
                                            <th>Mô tả</th>
                                            <th class="text-center">Số sản phẩm</th>
                                            <th>Hành động</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="c" items="${categories}">
                                            <tr>
                                                <td class="ps-4 fw-bold">#${c.id}</td>
                                                <td class="fw-bold text-primary">${c.name}</td>
                                                <td>${c.description}</td>

                                                <td class="text-center">
                                                    <c:if test="${not empty c.products and c.products.size() > 0}">
                                                        <span
                                                            class="badge bg-success rounded-pill">${c.products.size()}</span>
                                                    </c:if>
                                                    <c:if test="${empty c.products or c.products.size() == 0}">
                                                        <span class="badge bg-secondary rounded-pill">0</span>
                                                    </c:if>
                                                </td>

                                                <td>
                                                    <a href="/admin/category/update/${c.id}"
                                                        class="btn btn-sm btn-outline-warning me-1">
                                                        <i class="fas fa-edit"></i>
                                                    </a>

                                                    <a href="/admin/category/delete/${c.id}"
                                                        class="btn btn-sm btn-outline-danger"
                                                        onclick="return confirmDelete(${empty c.products ? 0 : c.products.size()})">
                                                        <i class="fas fa-trash-alt"></i>
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

            <script>
                function confirmDelete(count) {
                    if (count > 0) {
                        alert('CẢNH BÁO:\n\nDanh mục này đang có ' + count + ' sản phẩm.\nBạn KHÔNG THỂ xóa danh mục khi chưa xóa hết sản phẩm bên trong!');
                        return false; // Chặn hành động
                    } else {
                        return confirm('Bạn có chắc chắn muốn xóa danh mục này?');
                    }
                }
            </script>
        </body>

        </html>