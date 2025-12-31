package com.example.ipms.dto.auth;

import com.example.ipms.entity.enums.Role;
import lombok.Data;

@Data
public class RegisterRequest {
    private String fullName;
    private String email;
    private String password;
    private String phone;
    private String citizenId; // CCCD
    private String address;
    private Role role; // Cho phép chọn role lúc đăng ký (để test cho nhanh)
}