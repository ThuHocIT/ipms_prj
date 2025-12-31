package com.example.ipms.entity;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "trademark_details")
@Data
public class TrademarkDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "application_id", nullable = false)
    @JsonBackReference
    private Application application;

    @Column(name = "mark_type")
    private String markType; // Nhãn hiệu tập thể, chứng nhận...

    @Column(name = "mark_sample_url")
    private String markSampleUrl; // URL ảnh logo

    @Column(name = "mark_color")
    private String markColor;

    @Column(name = "mark_description", columnDefinition = "TEXT")
    private String markDescription;

    @Column(name = "nice_classes", columnDefinition = "TEXT")
    private String niceClasses; // Danh mục nhóm sản phẩm
}