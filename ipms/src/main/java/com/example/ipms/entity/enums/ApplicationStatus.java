package com.example.ipms.entity.enums;

public enum ApplicationStatus {
    NEW,                        // Đang tiếp nhận đơn
    WAITING_FEE,                // Chờ đóng phí
    FORMAL_EXAMINATION,         // Đang thẩm định hình thức
    FORMAL_ACCEPTED,            // Chấp nhận đơn hợp lệ về hình thức
    FORMAL_REJECT_INTENT,       // Thông báo dự định từ chối hình thức
    REJECTED,                   // Từ chối chấp nhận đơn
    WAITING_PUBLICATION,        // Đang chờ công bố
    PUBLISHED,                  // Đã công bố
    SUBSTANTIVE_EXAMINATION,    // Đang thẩm định nội dung
    SUBSTANTIVE_RESULT,         // Thông báo kết quả thẩm định nội dung
    GRANT_INTENT,               // Dự định cấp văn bằng
    GRANT_REJECT_INTENT,        // Dự định từ chối cấp văn bằng
    GRANTED                     // Đã cấp văn bằng
}