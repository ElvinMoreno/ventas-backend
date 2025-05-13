package com.testProject.productos.dto;

import java.time.LocalDateTime;

public interface ChatMessageProjection {
    Long getId();
    String getContent();
    LocalDateTime getTimestamp();
    String getDirection();
    String getStatus();
    String getChatId();
}
