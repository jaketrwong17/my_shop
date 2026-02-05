package com.example.shop.service;

import com.example.shop.domain.Role;
import com.example.shop.domain.User;
import com.example.shop.domain.dto.RegisterDTO;
import com.example.shop.repository.RoleRepository;
import com.example.shop.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender; // 1. Thêm JavaMailSender

    // Cập nhật Constructor để Inject JavaMailSender
    public UserService(UserRepository userRepository, RoleRepository roleRepository,
            PasswordEncoder passwordEncoder, JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
    }

    // Lưu thông tin người dùng vào cơ sở dữ liệu
    public User handleSaveUser(User user) {
        return userRepository.save(user);
    }

    // Đăng ký người dùng mới (CẬP NHẬT: Thêm logic tạo mã xác thực và set enabled =
    // false)
    public User registerUser(User user) {
        Role role = roleRepository.findByName("USER");
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // --- THÊM MỚI ---
        user.setVerificationCode(UUID.randomUUID().toString()); // Tạo mã xác thực
        user.setEnabled(false); // Mặc định chưa kích hoạt
        // ----------------

        return this.handleSaveUser(user);
    }

    // --- CÁC HÀM MỚI CHO CHỨC NĂNG XÁC THỰC EMAIL ---

    // Gửi email xác thực đăng ký
    public void sendVerificationEmail(User user, String siteURL)
            throws MessagingException, UnsupportedEncodingException {
        String toAddress = user.getEmail();
        String fromAddress = "jaketruong7@gmail.com";
        String senderName = "16Home Support";
        String subject = "Vui lòng xác thực đăng ký tài khoản";
        String content = "Chào [[name]],<br>"
                + "Vui lòng nhấn vào liên kết dưới đây để kích hoạt tài khoản của bạn:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">XÁC THỰC NGAY</a></h3>"
                + "Cảm ơn,<br>"
                + "16Home Team.";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]", user.getFullName());
        String verifyURL = siteURL + "/verify?code=" + user.getVerificationCode();
        content = content.replace("[[URL]]", verifyURL);

        helper.setText(content, true);
        mailSender.send(message);
    }

    // Xử lý khi người dùng bấm link xác thực
    public boolean verify(String verificationCode) {
        User user = userRepository.findByVerificationCode(verificationCode);

        if (user == null || user.isEnabled()) {
            return false; // Code sai hoặc đã kích hoạt rồi
        } else {
            user.setVerificationCode(null); // Xóa code đi
            user.setEnabled(true); // Kích hoạt tài khoản
            userRepository.save(user);
            return true;
        }
    }

    // --- CÁC HÀM MỚI CHO CHỨC NĂNG QUÊN MẬT KHẨU ---

    // 1. Tạo token reset password và lưu vào DB
    public void updateResetPasswordToken(String token, String email) throws Exception {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            user.setResetPasswordToken(token);
            userRepository.save(user);
        } else {
            throw new Exception("Không tìm thấy email: " + email);
        }
    }

    // 2. Tìm user bằng token reset password
    public User getByResetPasswordToken(String token) {
        return userRepository.findByResetPasswordToken(token);
    }

    // 3. Cập nhật mật khẩu mới và xóa token
    public void updatePassword(User user, String newPassword) {
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        user.setResetPasswordToken(null);
        userRepository.save(user);
    }

    // 4. Gửi email chứa link reset password
    public void sendEmail(String recipientEmail, String link) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("jaketruong7@gmail.com", "Shop Support");
        helper.setTo(recipientEmail);

        String subject = "Yêu cầu đặt lại mật khẩu";
        String content = "<p>Xin chào,</p>"
                + "<p>Bạn đã yêu cầu đặt lại mật khẩu.</p>"
                + "<p>Nhấn vào link bên dưới để đổi mật khẩu:</p>"
                + "<p><a href=\"" + link + "\">Đặt lại mật khẩu</a></p>"
                + "<br>"
                + "<p>Bỏ qua email này nếu bạn nhớ mật khẩu của mình.</p>";

        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(message);
    }

    // ---------------------------------------------------------

    // Tìm kiếm người dùng theo địa chỉ email
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Lấy danh sách tất cả người dùng trong hệ thống
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Tìm kiếm người dùng theo ID
    public User getUserById(long id) {
        return this.userRepository.findById(id).orElse(null);
    }

    // Đảo trạng thái khóa/mở khóa tài khoản người dùng
    public void toggleLockUser(long id) {
        User user = this.getUserById(id);
        if (user != null) {
            user.setIsLocked(!user.getIsLocked());
            this.userRepository.save(user);
        }
    }

    // Xóa người dùng và gỡ bỏ liên kết với giỏ hàng
    public void deleteUserById(long id) {
        User user = this.getUserById(id);
        if (user != null) {
            if (user.getCart() != null) {
                user.setCart(null);
            }
            userRepository.deleteById(id);
        }
    }

    // Lấy thông tin quyền hạn theo tên role
    public Role getRoleByName(String name) {
        return this.roleRepository.findByName(name);
    }

    // Chuyển đổi dữ liệu từ RegisterDTO sang thực thể User
    public User registerDTOtoUser(RegisterDTO registerDTO) {
        User user = new User();
        user.setFullName(registerDTO.getFirstName() + " " + registerDTO.getLastName());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(registerDTO.getPassword());
        return user;
    }

    // Kiểm tra xem email đã tồn tại trong hệ thống chưa
    public boolean checkEmailExists(String email) {
        return this.userRepository.existsByEmail(email);
    }

    // Cập nhật thông tin cá nhân của người dùng
    public void updateUserProfile(long userId, User updatedUser) {
        User currentUser = this.getUserById(userId);
        if (currentUser != null) {
            currentUser.setFullName(updatedUser.getFullName());
            currentUser.setPhone(updatedUser.getPhone());
            currentUser.setAddress(updatedUser.getAddress());
            this.userRepository.save(currentUser);
        }
    }

    // Đếm tổng số lượng người dùng trong hệ thống
    public long countAllUsers() {
        return userRepository.count();
    }
}