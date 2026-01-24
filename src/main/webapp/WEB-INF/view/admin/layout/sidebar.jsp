<%@ page contentType="text/html;charset=UTF-8" language="java" %>

    <div id="sidebar-wrapper">
        <div class="sidebar-heading">
            <i class="fas fa-rocket me-2"></i> Admin Pro
        </div>

        <div class="list-group list-group-flush mt-3">
            <a href="/admin"
                class="list-group-item list-group-item-action ${param.active == 'dashboard' ? 'active' : ''}">
                <i class="fas fa-tachometer-alt me-2"></i> Thống kê
            </a>

            <div class="text-uppercase small text-muted px-4 pt-4 pb-2">Quản lý</div>

            <a href="/admin/product"
                class="list-group-item list-group-item-action ${param.active == 'product' ? 'active' : ''}">
                <i class="fas fa-box me-2"></i> Sản phẩm
            </a>

            <a href="/admin/category"
                class="list-group-item list-group-item-action ${param.active == 'category' ? 'active' : ''}">
                <i class="fas fa-tags me-2"></i> Danh mục
            </a>

            <a href="/admin/order"
                class="list-group-item list-group-item-action ${param.active == 'order' ? 'active' : ''}">
                <i class="fas fa-shopping-cart me-2"></i> Đơn hàng
            </a>

            <a href="/admin/user"
                class="list-group-item list-group-item-action ${param.active == 'user' ? 'active' : ''}">
                <i class="fas fa-users me-2"></i> Người dùng
            </a>

            <a href="#" class="list-group-item list-group-item-action ${param.active == 'review' ? 'active' : ''}">
                <i class="fas fa-star me-2"></i> Đánh giá
            </a>
            <a href="/admin/voucher"
                class="list-group-item list-group-item-action ${param.active == 'voucher' ? 'active' : ''}">
                <i class="fas fa-ticket-alt me-2"></i> Mã giảm giá
            </a>

            <div class="mt-auto p-4">
                <a href="/logout" class="btn btn-outline-danger w-100">
                    <i class="fas fa-sign-out-alt me-2"></i> Đăng xuất
                </a>
            </div>
        </div>
    </div>