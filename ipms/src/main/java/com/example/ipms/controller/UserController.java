package com.example.ipms.controller;

import com.example.ipms.dto.auth.ChangePasswordRequest; // Sẽ tạo class này ở dưới
import com.example.ipms.entity.User;
import com.example.ipms.entity.enums.Role;
import com.example.ipms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder; // Cần cái này để mã hóa
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Inject bộ mã hóa mật khẩu

    // 1. API Lấy danh sách khách hàng (Đã làm ở bước trước)
    @GetMapping("/clients")
    public ResponseEntity<List<User>> getAllClients() {
        return ResponseEntity.ok(userRepository.findByRole(Role.CLIENT));
    }

    // 2. API Lấy thông tin chi tiết 1 user (để hiển thị lên form sửa)
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserProfile(@PathVariable Long userId) {
        return ResponseEntity.of(userRepository.findById(userId));
    }

    // 3. API Cập nhật thông tin cá nhân
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateProfile(@PathVariable Long userId, @RequestBody User updatedInfo) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Chỉ cho phép sửa các thông tin cơ bản
        user.setFullName(updatedInfo.getFullName());
        user.setPhone(updatedInfo.getPhone());
        user.setAddress(updatedInfo.getAddress());
        user.setCitizenId(updatedInfo.getCitizenId());

        // Lưu lại
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }

    // 4. API Đổi mật khẩu
    @PostMapping("/{userId}/change-password")
    public ResponseEntity<?> changePassword(@PathVariable Long userId, @RequestBody ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Bước 1: Kiểm tra mật khẩu cũ có đúng không
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Mật khẩu cũ không chính xác!");
        }

        // Bước 2: Mã hóa mật khẩu mới và lưu
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok("Đổi mật khẩu thành công!");
    }
}