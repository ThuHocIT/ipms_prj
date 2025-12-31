package com.example.ipms.repository;

import com.example.ipms.entity.GeoIndicationDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GeoIndicationDetailRepository extends JpaRepository<GeoIndicationDetail, Long> {
    GeoIndicationDetail findByApplication_ApplicationId(Long applicationId);
}