package com.example.ipms.repository;

import com.example.ipms.entity.User;
import com.example.ipms.entity.enums.Role;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Tìm user bằng email (dùng cho chức năng đăng nhập)
    Optional<User> findByEmail(String email);
    List<User> findByRole(Role role);

}