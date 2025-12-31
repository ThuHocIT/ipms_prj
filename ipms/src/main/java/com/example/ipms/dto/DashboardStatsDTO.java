package com.example.ipms.dto;

import lombok.Data;

@Data
public class DashboardStatsDTO {
    private long totalApps;      // Tổng số hồ sơ
    private long waitingFeeApps; // Số hồ sơ chờ đóng phí
    private long grantedApps;    // Số hồ sơ đã cấp bằng
}