package com.example.shop.config;

import com.example.shop.service.CustomUserDetailsService;
import com.example.shop.service.UserService;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.util.Collection;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserService userService) {
        return new CustomUserDetailsService(userService);
    }

    @Bean
    public DaoAuthenticationProvider authProvider(
            PasswordEncoder passwordEncoder,
            UserDetailsService userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    /**
     * BEAN QUAN TRỌNG NHẤT: Xử lý logic sau khi đăng nhập thành công
     * Nhiệm vụ:
     * 1. Lưu Email vào Session (để hiện tên, giỏ hàng).
     * 2. Lưu Role vào Session (để hiện nút Admin).
     */
    @Bean
    public AuthenticationSuccessHandler customSuccessHandler() {
        return (request, response, authentication) -> {
            HttpSession session = request.getSession();

            // 1. Lấy Email
            String email = authentication.getName();
            session.setAttribute("email", email);

            // 2. Lấy Role (Quyền)
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            String role = "";
            if (!authorities.isEmpty()) {
                // Lấy quyền đầu tiên (Ví dụ: ROLE_ADMIN hoặc ADMIN)
                role = authorities.iterator().next().getAuthority();
            }

            // [MẸO] Chuẩn hóa Role để bên JSP dễ so sánh
            // Nếu Spring Security trả về "ROLE_ADMIN", ta cắt bỏ "ROLE_" để chỉ còn "ADMIN"
            if (role.startsWith("ROLE_")) {
                role = role.substring(5);
            }

            // Lưu vào session: key="role", value="ADMIN" (hoặc USER)
            session.setAttribute("role", role);

            // 3. Chuyển hướng về trang chủ
            response.sendRedirect("/");
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
            AuthenticationSuccessHandler successHandler)
            throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.INCLUDE)
                        .permitAll()

                        // 1. Cho phép truy cập tài nguyên tĩnh và các trang công khai
                        .requestMatchers("/", "/login", "/register", "/product/**", "/client/**",
                                "/css/**", "/js/**", "/images/**", "/api/**") // Thêm /api/** cho nút Xem thêm
                        .permitAll()

                        // 2. Cho phép thao tác giỏ hàng mà không cần đăng nhập
                        .requestMatchers("/add-product-to-cart/**", "/cart", "/delete-cart-item/**",
                                "/update-cart-quantity/**", "/delete-multiple-cart-items/**")
                        .permitAll()

                        // 3. Trang Admin chỉ dành cho ADMIN
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // 4. Các trang còn lại bắt buộc phải đăng nhập
                        .anyRequest().authenticated())

                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login") // Spring Security xử lý POST tại đây
                        .successHandler(successHandler) // Gọi cái Bean mình vừa viết ở trên
                        .failureUrl("/login?error")
                        .permitAll())

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll());

        return http.build();
    }
}