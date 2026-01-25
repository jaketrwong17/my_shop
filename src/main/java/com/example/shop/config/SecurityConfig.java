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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

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

    // Xử lý logic sau khi đăng nhập thành công
    @Bean
    public AuthenticationSuccessHandler customSuccessHandler() {
        return (request, response, authentication) -> {
            HttpSession session = request.getSession();
            String email = authentication.getName(); // Lấy email người dùng
            session.setAttribute("email", email); // Lưu vào session để code giỏ hàng hoạt động

            // Chuyển hướng về trang chủ
            response.sendRedirect("/");
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationSuccessHandler successHandler)
            throws Exception {
        http
                // Trong SecurityConfig.java

                .authorizeHttpRequests(authorize -> authorize
                        .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.INCLUDE).permitAll()

                        // 1. Cho phép truy cập tài nguyên tĩnh và các trang công khai
                        .requestMatchers("/", "/login", "/register", "/product/**", "/client/**", "/css/**", "/js/**",
                                "/images/**")
                        .permitAll()

                        // 2. [QUAN TRỌNG] Cho phép Thêm/Sửa/Xóa giỏ hàng mà KHÔNG CẦN ĐĂNG NHẬP
                        .requestMatchers("/add-product-to-cart/**", "/cart", "/delete-cart-item/**",
                                "/update-cart-quantity/**", "/delete-multiple-cart-items/**")
                        .permitAll()

                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // 3. Các trang còn lại (Ví dụ: /checkout, /history) bắt buộc phải đăng nhập
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login") // Spring Security tự xử lý POST ở đây
                        .successHandler(successHandler) // Gắn handler tùy chỉnh ở trên
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