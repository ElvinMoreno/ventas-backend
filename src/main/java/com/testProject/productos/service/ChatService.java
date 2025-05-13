package com.testProject.productos.service;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.testProject.productos.dto.ChatMessageDTO;
import com.testProject.productos.dto.ChatMessageResponseDTO;
import com.testProject.productos.dto.OutboundMessageDTO;
import com.testProject.productos.model.Chat;
import com.testProject.productos.model.ChatMessage;
import com.testProject.productos.model.MessageDirection;
import com.testProject.productos.model.MessageStatus;
import com.testProject.productos.repository.ChatMessageRepository;
import com.testProject.productos.repository.ChatRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ChatService {
    private final ChatRepository chatRepository;
    private final ChatMessageRepository chatMessageRepository;
    
    @Autowired
    public ChatService(ChatRepository chatRepository, 
                     ChatMessageRepository chatMessageRepository) {
        this.chatRepository = chatRepository;
        this.chatMessageRepository = chatMessageRepository;
    }
    
    
    public Chat getOrCreateChat(String chatId) {
        return chatRepository.findByChatId(chatId)
                .orElseGet(() -> {
                    Chat newChat = new Chat();
                    newChat.setChatId(chatId);
                    return chatRepository.save(newChat);
                });
    }
    
    @Transactional
    public ChatMessageResponseDTO saveInboundMessage(String chatId, String messageContent) {
        Chat chat = getOrCreateChat(chatId);
        ChatMessage message = new ChatMessage();
        message.setChat(chat);
        message.setContent(messageContent);
        message.setDirection(MessageDirection.INBOUND);
        message.setStatus(MessageStatus.RECEIVED);
        chatMessageRepository.save(message);
        return new ChatMessageResponseDTO(message);
    }
    
    public ChatMessage saveOutboundMessage(String chatId, String message) {
        Chat chat = getOrCreateChat(chatId);
        
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setChat(chat);
        chatMessage.setContent(message);
        chatMessage.setDirection(MessageDirection.OUTBOUND);
        chatMessage.setStatus(MessageStatus.SENT);
        
        return chatMessageRepository.save(chatMessage);
    }
    
    public List<ChatMessageDTO> getChatHistory(String chatId) {
 
        Sort sort = Sort.by(Sort.Direction.DESC, "timestamp");
        Pageable pageRequest = PageRequest.of(0, 10, sort);
        
        return chatMessageRepository.findLastMessagesByChatId(chatId, pageRequest);
    }
    
    public ChatMessage updateMessageStatus(Long messageId, MessageStatus status) {
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        
        message.setStatus(status);
        return chatMessageRepository.save(message);
    }
    
    @Transactional
    public OutboundMessageDTO sendOutboundMessage(String chatId, String messageContent) {
        Chat chat = getOrCreateChat(chatId);
        
        ChatMessage message = new ChatMessage();
        message.setChat(chat);
        message.setContent(messageContent);
        message.setDirection(MessageDirection.OUTBOUND);
        message.setStatus(MessageStatus.SENT);
        
        chatMessageRepository.save(message);
        
        
        return new OutboundMessageDTO(message);
    }
    
    
    
}