package com.example.ipms.controller;

import com.example.ipms.entity.ChatMessage;
import com.example.ipms.entity.User;
import com.example.ipms.repository.ChatMessageRepository;
import com.example.ipms.repository.UserRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@CrossOrigin
public class ChatController {

    @Autowired
    private ChatMessageRepository chatRepo;
    @Autowired
    private UserRepository userRepo;

    // 1. API Gửi tin nhắn qua WebSocket
    // Client gửi tới: /app/chat.sendMessage
    // Server bắn ra: /topic/public
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public") // Ai đăng ký kênh này sẽ nhận được tin
    public ChatMessageDTO sendMessage(@Payload ChatMessageDTO chatMessageDto) {
        // Lưu vào DB
        User sender = userRepo.findById(chatMessageDto.getSenderId()).orElse(null);

        ChatMessage msg = new ChatMessage();
        msg.setApplicationId(chatMessageDto.getApplicationId());
        msg.setSender(sender);
        msg.setContent(chatMessageDto.getContent());
        msg.setSentAt(LocalDateTime.now());

        chatRepo.save(msg);

        // Trả về DTO để hiển thị ngay lập tức
        chatMessageDto.setSenderName(sender.getFullName());
        chatMessageDto.setSentAt(msg.getSentAt().toString());
        return chatMessageDto;
    }

    // 2. API Lấy lịch sử tin nhắn (REST)
    @GetMapping("/api/chat/{appId}")
    public ResponseEntity<List<ChatMessage>> getHistory(@PathVariable Long appId) {
        return ResponseEntity.ok(chatRepo.findByApplicationIdOrderBySentAtAsc(appId));
    }
}

// Class DTO phụ trợ (để trong cùng file cho gọn hoặc tách ra)
@Data
class ChatMessageDTO {
    private Long applicationId;
    private Long senderId;
    private String senderName;
    private String content;
    private String sentAt;
}