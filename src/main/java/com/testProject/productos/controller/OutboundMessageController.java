package com.testProject.productos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.testProject.productos.dto.ApiResponseDTO;
import com.testProject.productos.dto.OutboundMessageDTO;
import com.testProject.productos.model.ChatMessage;
import com.testProject.productos.model.MessageStatus;
import com.testProject.productos.service.ChatService;

import lombok.Data;


@RestController
@RequestMapping("/api/chat/outbound")
public class OutboundMessageController {
    private final ChatService chatService;
    
    @Autowired
    public OutboundMessageController(ChatService chatService) {
        this.chatService = chatService;
    }
    
    @PostMapping()
    public ResponseEntity<ApiResponseDTO<OutboundMessageDTO>> sendOutboundMessage(
            @RequestBody OutboundMessageRequest request) {
        
        OutboundMessageDTO sentMessage = chatService.sendOutboundMessage(
                request.getChatId(), 
                request.getMessage());
        
        return ResponseEntity.ok(ApiResponseDTO.success(sentMessage));
    }
    
    @PutMapping("/{messageId}/status")
    public ResponseEntity<ApiResponseDTO<ChatMessage>> updateMessageStatus(
            @PathVariable Long messageId,
            @RequestParam MessageStatus status) {
        
        ChatMessage message = chatService.updateMessageStatus(messageId, status);
        return ResponseEntity.ok(ApiResponseDTO.success(message));
    }
}

@Data
class OutboundMessageRequest {
    private String chatId;
    private String message;
}