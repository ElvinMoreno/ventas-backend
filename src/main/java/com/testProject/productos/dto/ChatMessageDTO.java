package com.testProject.productos.dto;

import java.time.LocalDateTime;

import com.testProject.productos.model.ChatMessage;
import com.testProject.productos.model.MessageDirection;
import com.testProject.productos.model.MessageStatus;

import lombok.Data;

@Data
public class ChatMessageDTO {
	
	private Long id;
    private String content;
    private LocalDateTime timestamp;
    private String direction;
    private String status;
    private String chatId;

    public ChatMessageDTO(Long id, String content, LocalDateTime timestamp, 
                         MessageDirection direction, MessageStatus status, String chatId) {
        this.id = id;
        this.content = content;
        this.timestamp = timestamp;
        this.direction = direction.name();
        this.status = status.name();
        this.chatId = chatId;
    }
    
   
}
