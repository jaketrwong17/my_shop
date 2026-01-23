package com.example.shop.service;

import com.example.shop.domain.User;
import com.example.shop.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. Tìm user trong DB bằng email
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("Không tìm thấy user với email: " + email);
        }

        // 2. Tạo quyền (Role) cho Spring Security hiểu
        // Spring Security yêu cầu Role phải có tiền tố "ROLE_"
        String roleName = user.getRole().getName(); // VD: "ADMIN" hoặc "CUSTOMER"
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + roleName);

        // 3. Trả về đối tượng User của Spring Security
        // (org.springframework.security.core.userdetails.User)
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(authority));
    }
}