package com.example.ipms.repository;

import com.example.ipms.entity.TrademarkDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrademarkDetailRepository extends JpaRepository<TrademarkDetail, Long> {
    // Tìm chi tiết nhãn hiệu dựa trên ID của hồ sơ cha
    TrademarkDetail findByApplication_ApplicationId(Long applicationId);
}