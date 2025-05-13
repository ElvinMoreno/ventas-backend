package com.testProject.productos.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.testProject.productos.dto.ApiResponseDTO;
import com.testProject.productos.dto.ChatMessageDTO;
import com.testProject.productos.dto.ChatMessageResponseDTO;
import com.testProject.productos.model.ChatMessage;
import com.testProject.productos.service.ChatService;

import lombok.Data;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatService chatService;
    
    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }
    
    @PostMapping("/inbound")
    public ResponseEntity<ApiResponseDTO<ChatMessageResponseDTO>> handleInboundMessage(
            @RequestBody InboundMessageRequest request) {
        
        ChatMessageResponseDTO savedMessage = chatService.saveInboundMessage(
                request.getChatId(), 
                request.getMessage());
        
        return ResponseEntity.ok(ApiResponseDTO.success(savedMessage));
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
    private String chatId;
    private String message;
}