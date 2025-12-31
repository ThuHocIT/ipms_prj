package com.example.ipms.service;

import com.example.ipms.dto.auth.LoginRequest;
import com.example.ipms.dto.auth.RegisterRequest;
import com.example.ipms.entity.User;
import com.example.ipms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Chức năng Đăng ký
    public User register(RegisterRequest request) {
        // 1. Kiểm tra email đã tồn tại chưa
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email đã tồn tại!");
        }

        // 2. Tạo user mới từ request
        User newUser = new User();
        newUser.setFullName(request.getFullName());
        newUser.setEmail(request.getEmail());
        // 3. Mã hóa mật khẩu trước khi lưu
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setPhone(request.getPhone());
        newUser.setCitizenId(request.getCitizenId());
        newUser.setAddress(request.getAddress());
        newUser.setRole(request.getRole());

        // 4. Lưu xuống DB
        return userRepository.save(newUser);
    }

    // Chức năng Đăng nhập (Đơn giản)
    public User login(LoginRequest request) {
        // 1. Tìm user theo email
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isEmpty()) {
            throw new RuntimeException("Email không tồn tại!");
        }

        User user = userOpt.get();

        // 2. So khớp mật khẩu nhập vào (request) với mật khẩu đã mã hóa (DB)
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu không đúng!");
        }

        return user; // Đăng nhập thành công trả về thông tin User
    }
}