package com.example.ipms.repository;

import com.example.ipms.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    // Lấy lịch sử chat của 1 hồ sơ
    List<ChatMessage> findByApplicationIdOrderBySentAtAsc(Long applicationId);
}