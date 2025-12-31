package com.example.ipms.service;

import com.example.ipms.entity.*;
import com.example.ipms.entity.enums.ApplicationType;
import com.example.ipms.repository.*;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WordExportService {

    @Autowired
    private ApplicationRepository appRepo;
    @Autowired
    private TrademarkDetailRepository trademarkRepo;
    @Autowired
    private GeoIndicationDetailRepository geoRepo;

    public byte[] exportApplication(Long appId) throws Exception {
        Application app = appRepo.findById(appId)
                .orElseThrow(() -> new RuntimeException("App not found"));
        User client = app.getClient();

        // 1. Chuẩn bị dữ liệu Text
        Map<String, String> data = new HashMap<>();
        data.put("{{FULL_NAME}}", client.getFullName() != null ? client.getFullName() : "");
        data.put("{{ADDRESS}}", client.getAddress() != null ? client.getAddress() : "");
        data.put("{{PHONE}}", client.getPhone() != null ? client.getPhone() : "");
        data.put("{{EMAIL}}", client.getEmail() != null ? client.getEmail() : "");

        String templateName = "";
        String imageUrl = null; // Biến để lưu link ảnh

        // 2. Lấy dữ liệu chi tiết tùy loại hồ sơ
        if (app.getAppType() == ApplicationType.TRADEMARK) {
            templateName = "templates/template_trademark.docx";
            TrademarkDetail detail = trademarkRepo.findByApplication_ApplicationId(appId);
            if (detail != null) {
                data.put("{{MARK_DESC}}", detail.getMarkDescription());
                data.put("{{MARK_COLOR}}", detail.getMarkColor());
                data.put("{{NICE_CLASSES}}", detail.getNiceClasses());

                // Lấy URL ảnh nếu có
                if (detail.getMarkSampleUrl() != null && !detail.getMarkSampleUrl().isEmpty()) {
                    imageUrl = detail.getMarkSampleUrl();
                }
            }
        } else {
            templateName = "templates/template_geo.docx";
            GeoIndicationDetail detail = geoRepo.findByApplication_ApplicationId(appId);
            if (detail != null) {
                data.put("{{PROD_NAME}}", detail.getProductName());
                data.put("{{PROD_DESC}}", detail.getProductDescription());
                data.put("{{ORIGIN}}", detail.getOriginCountry());
            }
        }

        // 3. Xử lý file Word
        ClassPathResource resource = new ClassPathResource(templateName);
        try (InputStream is = resource.getInputStream();
             XWPFDocument document = new XWPFDocument(is);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // Quét qua các đoạn văn (Paragraphs)
            processParagraphs(document.getParagraphs(), data, imageUrl);

            // Quét qua các bảng (Tables)
            for (XWPFTable tbl : document.getTables()) {
                for (XWPFTableRow row : tbl.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        processParagraphs(cell.getParagraphs(), data, imageUrl);
                    }
                }
            }

            document.write(out);
            return out.toByteArray();
        }
    }

    // Helper: Duyệt danh sách đoạn văn để tìm từ khóa
    private void processParagraphs(List<XWPFParagraph> paragraphs, Map<String, String> data, String imageUrl) {
        for (XWPFParagraph p : paragraphs) {
            String text = p.getText();
            // Nếu gặp từ khóa ảnh -> Gọi hàm chèn ảnh
            if (imageUrl != null && text.contains("{{MARK_SAMPLE}}")) {
                replaceImageInParagraph(p, imageUrl);
            }
            // Nếu gặp từ khóa text -> Gọi hàm thay text
            else {
                replaceTextInParagraph(p, data);
            }
        }
    }

    // Helper: Thay thế Text
    private void replaceTextInParagraph(XWPFParagraph p, Map<String, String> data) {
        List<XWPFRun> runs = p.getRuns();
        if (runs != null) {
            String text = p.getText();
            boolean found = false;
            for (Map.Entry<String, String> entry : data.entrySet()) {
                if (text.contains(entry.getKey())) {
                    text = text.replace(entry.getKey(), entry.getValue());
                    found = true;
                }
            }
            if (found) {
                // Xóa nội dung cũ, ghi nội dung mới
                for (int i = runs.size() - 1; i >= 0; i--) p.removeRun(i);
                XWPFRun newRun = p.createRun();
                newRun.setText(text);
                newRun.setFontFamily("Times New Roman");
                newRun.setFontSize(12);
            }
        }
    }

    // Helper: Chèn Ảnh từ URL
    private void replaceImageInParagraph(XWPFParagraph p, String imageUrl) {
        // Xóa chữ {{MARK_SAMPLE}}
        for (int i = p.getRuns().size() - 1; i >= 0; i--) {
            p.removeRun(i);
        }

        XWPFRun run = p.createRun();
        try {
            URL url = new URL(imageUrl);
            InputStream imageStream = url.openStream();

            // Chèn ảnh kích thước 200x200 pixels
            run.addPicture(imageStream,
                    XWPFDocument.PICTURE_TYPE_PNG, // Mặc định xử lý PNG (hoặc JPEG)
                    "mark_sample",
                    Units.toEMU(200),
                    Units.toEMU(200));
            imageStream.close();
        } catch (Exception e) {
            run.setText("[Lỗi ảnh]");
            System.err.println("Lỗi tải ảnh: " + e.getMessage());
        }
    }
}