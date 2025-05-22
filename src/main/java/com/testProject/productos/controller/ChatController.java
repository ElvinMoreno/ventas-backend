package com.testProject.productos.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.testProject.productos.dto.ApiResponseDTO;
import com.testProject.productos.dto.ChatMessageDTO;
import com.testProject.productos.dto.ChatMessageResponseDTO;
import com.testProject.productos.model.ChatMessage;
import com.testProject.productos.service.ChatService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatService chatService;
    private static final Logger log = LoggerFactory.getLogger(ChatService.class);
    
    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }
    
    @PostMapping("/inbound")
    public ResponseEntity<ApiResponseDTO<ChatMessageResponseDTO>> handleInboundMessage(
            @Valid @RequestBody InboundMessageRequest request) {
        
        try {
            String normalizedContent = validateAndNormalizeContent(request.getMessage());
            
            ChatMessageResponseDTO savedMessage = chatService.saveInboundMessage(
                    request.getChatId(), 
                    normalizedContent);
            
            return ResponseEntity.ok(ApiResponseDTO.success(savedMessage));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseDTO.notFound(e.getMessage()));
        } catch (Exception e) {
            log.error("Error processing inbound message for chat: {}", request.getChatId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.notFound("Error processing message"));
        }
    }

    private String validateAndNormalizeContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Message content cannot be empty");
        }

        String normalized = content.trim()
                .replaceAll("\r\n", "\n") 
                .replaceAll("\n+", "\n")   
                .replaceAll(" +", " ");  
     
        if (normalized.length() > 2000) {
            throw new IllegalArgumentException("Message content exceeds maximum length (2000 characters)");
        }
        
        return normalized;
    }
    
    @GetMapping("/history/{chatId}")
    public ResponseEntity<ApiResponseDTO<List<ChatMessageDTO>>> getChatHistory(
            @PathVariable String chatId) {
        
        try {
            List<ChatMessageDTO> history = chatService.getChatHistory(chatId);
            if (history.isEmpty()) {
                return ResponseEntity.ok(ApiResponseDTO.notFound("No se encontró historial para el chat"));
            }
            return ResponseEntity.ok(ApiResponseDTO.success(history));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.notFound("Error al obtener el historial"));
        }
    }
}

@Data
class InboundMessageRequest {
    @NotBlank(message = "chatId cannot be blank")
    private String chatId;
    
    @NotBlank(message = "message content cannot be blank")
    @Size(max = 2000, message = "message content must not exceed 2000 characters")
    private String message;
}