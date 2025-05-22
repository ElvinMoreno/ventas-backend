package com.testProject.productos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chat_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    @Size(max = 2000, message = "Message content must not exceed 2000 characters")
    private String content;
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MessageDirection direction;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MessageStatus status;
    
    
    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now();
    }
}