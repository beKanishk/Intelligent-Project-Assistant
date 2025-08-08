package com.assistant.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.assistant.model.Message;
import com.assistant.model.AssistantSession;
import java.util.List;
import com.assistant.model.User;


public interface MessageRepository extends JpaRepository<Message, Long>{
	List<Message> findBySessionAndUser(AssistantSession session, User user);
}
