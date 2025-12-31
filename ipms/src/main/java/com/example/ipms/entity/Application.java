package com.example.ipms.entity;

import com.example.ipms.entity.enums.ApplicationStatus;
import com.example.ipms.entity.enums.ApplicationType;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "applications")
@Data
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_id")
    private Long applicationId;

    // Quan hệ N-1: Nhiều đơn thuộc về 1 User (Khách hàng)
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User client;

    // Quan hệ N-1: Nhiều đơn được xử lý bởi 1 Consultant
    @ManyToOne
    @JoinColumn(name = "consultant_id")
    private User consultant;

    @OneToOne(mappedBy = "application", cascade = CascadeType.ALL)
    @JsonManagedReference // Giúp hiển thị chi tiết con khi gọi API cha
    private TrademarkDetail trademarkDetail;

    @OneToOne(mappedBy = "application", cascade = CascadeType.ALL)
    @JsonManagedReference
    private GeoIndicationDetail geoIndicationDetail;

    @Enumerated(EnumType.STRING)
    @Column(name = "app_type")
    private ApplicationType appType;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status = ApplicationStatus.NEW;

    @Column(name = "filing_date")
    private LocalDate filingDate;

    @Column(name = "application_number")
    private String applicationNumber;

    @Column(name = "total_fee")
    private BigDecimal totalFee;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}