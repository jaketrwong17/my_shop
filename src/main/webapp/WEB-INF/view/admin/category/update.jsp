<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

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
                        <h4 class="mb-0 text-dark fw-bold">Cập nhật Danh mục</h4>
                    </nav>

                    <div class="container-fluid px-4 py-4">
                        <div class="row justify-content-center">
                            <div class="col-lg-6">
                                <div class="card shadow-sm border-0 rounded-3">
                                    <div class="card-body p-4">
                                        <form:form action="/admin/category/update" method="POST"
                                            modelAttribute="newCategory">

                                            <form:hidden path="id" />

                                            <div class="mb-3">
                                                <label class="form-label fw-bold">Tên danh mục <span
                                                        class="text-danger">*</span></label>
                                                <form:input path="name" class="form-control form-control-lg"
                                                    required="true" />
                                            </div>

                                            <div class="mb-4">
                                                <label class="form-label fw-bold">Mô tả</label>
                                                <form:textarea path="description" class="form-control" rows="4" />
                                            </div>

                                            <div class="d-grid gap-2">
                                                <button type="submit" class="btn btn-warning btn-lg text-white">Cập
                                                    nhật</button>
                                                <a href="/admin/category"
                                                    class="btn btn-light btn-lg text-secondary">Hủy bỏ</a>
                                            </div>
                                        </form:form>
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