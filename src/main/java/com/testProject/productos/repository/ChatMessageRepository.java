package com.testProject.productos.repository;

import org.springframework.data.domain.Pageable;
import java.util.List;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.testProject.productos.dto.ChatMessageDTO;
import com.testProject.productos.dto.ChatMessageProjection;
import com.testProject.productos.model.ChatMessage;
import com.testProject.productos.model.MessageDirection;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    @Query("SELECT m FROM ChatMessage m WHERE m.chat.chatId = :chatId ORDER BY m.timestamp ASC")
    List<ChatMessage> findByChat_ChatIdOrderByTimestampAsc(@Param("chatId") String chatId);
    
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chat.chatId = :chatId " +
           "AND cm.direction = :direction ORDER BY cm.timestamp DESC")
    List<ChatMessage> findLastMessagesByChatAndDirection(
            @Param("chatId") String chatId,
            @Param("direction") MessageDirection direction,
            Pageable pageable);
    
    @Query("SELECT m.id as id, m.content as content, m.timestamp as timestamp, " +
    	       "m.direction as direction, m.status as status, c.chatId as chatId " +
    	       "FROM ChatMessage m JOIN m.chat c WHERE c.chatId = :chatId ORDER BY m.timestamp ASC")
    	List<ChatMessageProjection> findProjectionsByChatId(@Param("chatId") String chatId);
  
    @Query("SELECT new com.testProject.productos.dto.ChatMessageDTO(" +
            "m.id, m.content, m.timestamp, m.direction, m.status, c.chatId) " +
            "FROM ChatMessage m JOIN m.chat c WHERE c.chatId = :chatId ORDER BY m.timestamp ASC")
     List<ChatMessageDTO> findMessageDTOsByChatId(@Param("chatId") String chatId);
    
    @Query("SELECT new com.testProject.productos.dto.ChatMessageDTO(" +
            "m.id, m.content, m.timestamp, m.direction, m.status, c.chatId) " +
            "FROM ChatMessage m JOIN m.chat c WHERE c.chatId = :chatId " +
            "ORDER BY m.timestamp DESC")
     List<ChatMessageDTO> findLastMessagesByChatId(
             @Param("chatId") String chatId,
             org.springframework.data.domain.Pageable pageable);
}