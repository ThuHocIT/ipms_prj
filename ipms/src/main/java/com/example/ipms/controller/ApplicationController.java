package com.example.ipms.controller;

import com.example.ipms.dto.DashboardStatsDTO;
import com.example.ipms.dto.application.CreateAppRequest;
import com.example.ipms.entity.Application;
import com.example.ipms.entity.enums.ApplicationStatus;
import com.example.ipms.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    @Autowired
    private ApplicationService appService;

    // 1. Tạo hồ sơ mới
    @PostMapping("/create")
    public ResponseEntity<?> createApplication(@RequestBody CreateAppRequest request) {
        try {
            Application app = appService.createApplication(request);
            return ResponseEntity.ok(app);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 2. Lấy danh sách hồ sơ của 1 User (Client xem)
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Application>> getMyApplications(@PathVariable Long userId) {
        return ResponseEntity.ok(appService.getApplicationsByUserId(userId));
    }

    // 3. Lấy tất cả hồ sơ (Consultant xem)
    @GetMapping("/all")
    public ResponseEntity<List<Application>> getAllApplications() {
        return ResponseEntity.ok(appService.getAllApplications());
    }

    // 4. Cập nhật trạng thái (Consultant dùng)
    @PutMapping("/{appId}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long appId, @RequestParam ApplicationStatus status) {
        return ResponseEntity.ok(appService.updateStatus(appId, status));
    }

    @Autowired
    private com.example.ipms.service.WordExportService wordService;

    // API Xuất file Word
    @GetMapping("/{appId}/export")
    public ResponseEntity<byte[]> exportApplication(@PathVariable Long appId) {
        try {
            byte[] content = wordService.exportApplication(appId);

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=ToKhai_" + appId + ".docx")
                    .body(content);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // API lấy thống kê Dashboard
    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {
        return ResponseEntity.ok(appService.getDashboardStats());
    }
}