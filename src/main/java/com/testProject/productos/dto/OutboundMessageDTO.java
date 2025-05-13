package com.testProject.productos.dto;

import java.time.LocalDateTime;

import com.testProject.productos.model.ChatMessage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OutboundMessageDTO {
    private Long id;
    private String content;
    private LocalDateTime timestamp;
    private String status;
    private String chatId;

    public OutboundMessageDTO(ChatMessage message) {
        this.id = message.getId();
        this.content = message.getContent();
        this.timestamp = message.getTimestamp();
        this.status = message.getStatus().name();
        this.chatId = message.getChat().getChatId();
    }
}