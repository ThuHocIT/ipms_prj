package com.example.ipms.dto.application;

import com.example.ipms.entity.enums.ApplicationType;
import lombok.Data;

@Data
public class CreateAppRequest {
    private Long userId; // ID của khách hàng nộp đơn
    private ApplicationType appType; // TRADEMARK hoặc GEO_INDICATION

    // --- Phần dành riêng cho Nhãn hiệu ---
    private String markType;        // VD: Nhãn hiệu tập thể
    private String markColor;       // Màu sắc
    private String markDescription; // Mô tả
    private String niceClasses;     // Nhóm sản phẩm (VD: Nhóm 1; Nhóm 2)
    private String markSampleUrl;   // Link ảnh (tạm thời gửi string)

    // --- Phần dành riêng cho Chỉ dẫn địa lý ---
    private String productName;        // Tên sản phẩm
    private String productDescription; // Tính chất đặc thù
    private String originCountry;      // Nước xuất xứ
}