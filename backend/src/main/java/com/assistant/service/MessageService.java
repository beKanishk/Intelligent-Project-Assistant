package com.assistant.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.assistant.dto.MessageRequest;
import com.assistant.model.AssistantSession;
import com.assistant.model.Message;
import com.assistant.model.User;
import com.assistant.repository.AssistantSessionRepository;
import com.assistant.repository.MessageRepository;

@Service
public class MessageService {
	@Autowired
	private MessageRepository messageRepository;
	
	@Autowired
	private AssistantSessionRepository assistantSessionRepository;
	
	@Autowired
	private UserService userService;
	
	public void handleIncomingMessage(MessageRequest request, String jwt) {
		Optional<AssistantSession> session = assistantSessionRepository.findById(request.getSessionId());
		User user = userService.findUserByJwt(jwt);
		
		Message msg = new Message(request.getRole(), request.getContent(), request.getTools(), LocalDateTime.now(), session.get(), user);
		messageRepository.save(msg);
	}
	
	public List<Message> getMessage(Long sessionId, String jwt) {
		Optional<AssistantSession> session = assistantSessionRepository.findById(sessionId);
		User user = userService.findUserByJwt(jwt);
		
		List<Message> msg = messageRepository.findBySessionAndUser(session.get(), user);
		return msg;
	}
}
