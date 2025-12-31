package com.example.ipms.repository;

import com.example.ipms.entity.Application;
import com.example.ipms.entity.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    // Tìm danh sách đơn của 1 khách hàng cụ thể
    List<Application> findByClient_UserId(Long userId);

    // Tìm danh sách đơn do 1 nhân viên phụ trách
    List<Application> findByConsultant_UserId(Long userId);

    // Hàm đếm số lượng hồ sơ theo trạng thái
    long countByStatus(ApplicationStatus status);
}