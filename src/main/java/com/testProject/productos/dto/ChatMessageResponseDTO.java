package com.testProject.productos.dto;

import java.time.LocalDateTime;

import com.testProject.productos.model.ChatMessage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponseDTO {
 private Long id;
 private String content;
 private LocalDateTime timestamp;
 private String direction;
 private String status;
 private String chatId; 

 public ChatMessageResponseDTO(ChatMessage message) {
     this.id = message.getId();
     this.content = message.getContent();
     this.timestamp = message.getTimestamp();
     this.direction = message.getDirection().name();
     this.status = message.getStatus().name();
     this.chatId = message.getChat().getChatId();
 }
}

