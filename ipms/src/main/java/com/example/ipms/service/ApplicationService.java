package com.example.ipms.service;

import com.example.ipms.dto.DashboardStatsDTO;
import com.example.ipms.dto.application.CreateAppRequest;
import com.example.ipms.entity.*;
import com.example.ipms.entity.enums.ApplicationStatus;
import com.example.ipms.entity.enums.ApplicationType;
import com.example.ipms.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class ApplicationService {

    @Autowired
    private ApplicationRepository appRepository;
    @Autowired
    private TrademarkDetailRepository trademarkRepository;
    @Autowired
    private GeoIndicationDetailRepository geoRepository;
    @Autowired
    private UserRepository userRepository;

    // 1. Tạo hồ sơ mới
    @Transactional // Đảm bảo nếu lưu chi tiết lỗi thì không lưu application
    public Application createApplication(CreateAppRequest request) {
        // Tìm người dùng
        User client = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Tạo Application chung
        Application app = new Application();
        app.setClient(client);
        app.setAppType(request.getAppType());
        app.setStatus(ApplicationStatus.NEW); // Mặc định là 'Đang tiếp nhận'
        app.setFilingDate(LocalDate.now());

        // LOGIC TÍNH PHÍ (Hardcode theo yêu cầu đồ án)
        if (request.getAppType() == ApplicationType.TRADEMARK) {
            app.setTotalFee(new BigDecimal("1000000")); // Ví dụ: 1 triệu
        } else {
            app.setTotalFee(new BigDecimal("2000000")); // Chỉ dẫn địa lý đắt hơn
        }

        // Lưu bảng chính trước để lấy ID
        Application savedApp = appRepository.save(app);

        // Lưu bảng chi tiết tùy loại
        if (request.getAppType() == ApplicationType.TRADEMARK) {
            TrademarkDetail detail = new TrademarkDetail();
            detail.setApplication(savedApp);
            detail.setMarkType(request.getMarkType());
            detail.setMarkColor(request.getMarkColor());
            detail.setMarkDescription(request.getMarkDescription());
            detail.setNiceClasses(request.getNiceClasses());
            detail.setMarkSampleUrl(request.getMarkSampleUrl());
            trademarkRepository.save(detail);

        } else if (request.getAppType() == ApplicationType.GEO_INDICATION) {
            GeoIndicationDetail detail = new GeoIndicationDetail();
            detail.setApplication(savedApp);
            detail.setProductName(request.getProductName());
            detail.setProductDescription(request.getProductDescription());
            detail.setOriginCountry(request.getOriginCountry());
            geoRepository.save(detail);
        }

        return savedApp;
    }

    // 2. Lấy danh sách hồ sơ (Admin/Consultant xem hết, Client xem của mình)
    public List<Application> getApplicationsByUserId(Long userId) {
        return appRepository.findByClient_UserId(userId);
    }

    public List<Application> getAllApplications() {
        return appRepository.findAll();
    }

    // 3. Cập nhật trạng thái (Dành cho Nhân viên)
    public Application updateStatus(Long appId, ApplicationStatus newStatus) {
        Application app = appRepository.findById(appId)
                .orElseThrow(() -> new RuntimeException("App not found"));
        app.setStatus(newStatus);
        return appRepository.save(app);
    }

    public DashboardStatsDTO getDashboardStats() {
        DashboardStatsDTO stats = new DashboardStatsDTO();

        // 1. Đếm tổng số
        stats.setTotalApps(appRepository.count());

        // 2. Đếm số đang chờ đóng phí
        stats.setWaitingFeeApps(appRepository.countByStatus(ApplicationStatus.WAITING_FEE));

        // 3. Đếm số đã cấp bằng thành công
        stats.setGrantedApps(appRepository.countByStatus(ApplicationStatus.GRANTED));

        return stats;
    }
}