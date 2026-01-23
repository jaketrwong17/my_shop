// package com.example.shop.config;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

// @Configuration
// @EnableWebSecurity
// public class SecurityConfig {

//     @Bean
//     public PasswordEncoder passwordEncoder() {
//         return new BCryptPasswordEncoder(); // Mã hóa mật khẩu chuẩn BCrypt
//     }

//     @Bean
//     public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//         http
//                 .authorizeHttpRequests(authorize -> authorize
//                         // 1. Cho phép truy cập tự do (Không cần đăng nhập)
//                         .requestMatchers("/", "/login", "/register", "/product/**", "/css/**", "/js/**", "/images/**")
//                         .permitAll()

//                         // 2. Chỉ ADMIN mới được vào trang quản trị
//                         .requestMatchers("/admin/**").hasRole("ADMIN")
//                         // Lưu ý: Trong DB role tên là "ADMIN", Spring sẽ tự hiểu là "ROLE_ADMIN"

//                         // 3. Các trang còn lại (Giỏ hàng, Đặt hàng) phải đăng nhập mới được vào
//                         .anyRequest().authenticated())
//                 .formLogin(form -> form
//                         .loginPage("/login") // Trang login của mình thiết kế
//                         .defaultSuccessUrl("/", true) // Đăng nhập thành công thì về trang chủ
//                         .permitAll())
//                 .logout(logout -> logout
//                         .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
//                         .logoutSuccessUrl("/login?logout") // Logout xong về lại trang login
//                         .permitAll());

//         return http.build();
//     }
// }
package com.example.shop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. Cho phép tất cả các request truy cập mà không cần login
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().permitAll())
                // 2. Tắt CSRF để test post form không bị lỗi 403
                .csrf(csrf -> csrf.disable())

                // 3. Tắt form login mặc định
                .formLogin(form -> form.disable());

        return http.build();
    }
}