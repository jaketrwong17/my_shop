<div class="row mt-5">
    <div class="col-lg-8">
        <div class="detail-content bg-white p-4 rounded shadow-sm">
            <h4 class="fw-bold mb-4">Thông tin chi tiết</h4>
            <p>${product.detailDesc}</p>
        </div>
    </div>

    <div class="col-lg-4">
        <div class="specs-box bg-white p-3 rounded shadow-sm border">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h5 class="fw-bold mb-0">Thông số nổi bật</h5>
                <button class="btn btn-primary btn-sm rounded-pill px-3">Xem chi tiết</button>
            </div>

            <div class="table-responsive">
                <table class="table table-bordered mb-0">
                    <tbody>
                        <c:forEach var="s" items="${product.specs}" varStatus="status">
                            <tr class="${status.index % 2 == 0 ? 'bg-light' : ''}">
                                <td class="text-secondary py-2 px-3 small" style="width: 45%; border-color: #eee;">
                                    ${s.specName}
                                </td>
                                <td class="py-2 px-3 small fw-medium" style="border-color: #eee;">
                                    ${s.specValue}
                                </td>
                            </tr>
                        </c:forEach>

                        <c:if test="${empty product.specs}">
                            <tr>
                                <td colspan="2" class="text-center text-muted py-3 small">
                                    Đang cập nhật thông số...
                                </td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<style>
    /* CSS bổ sung để giống hệt ảnh mẫu */
    .specs-box {
        position: sticky;
        top: 20px;
        /* Khi cuộn trang thì bảng thông số sẽ trượt theo */
    }

    .bg-light {
        background-color: #f9f9f9 !important;
        /* Màu xám nhạt xen kẽ */
    }

    .table-bordered td {
        border: 1px solid #f0f0f0;
    }

    .fw-medium {
        font-weight: 500;
    }

    h5 {
        font-size: 1.1rem;
    }
</style>