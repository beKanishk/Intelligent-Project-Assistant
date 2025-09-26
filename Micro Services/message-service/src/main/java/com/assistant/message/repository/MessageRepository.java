package com.assistant.message.repository;

import com.assistant.message.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySessionIdAndUserId(String sessionId, Long userId);

    boolean existsBySessionId(String sessionId);

    void deleteBySessionId(String sessionId);
}
