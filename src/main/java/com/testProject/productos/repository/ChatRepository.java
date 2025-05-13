package com.testProject.productos.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.testProject.productos.model.Chat;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    Optional<Chat> findByChatId(String chatId);
}
