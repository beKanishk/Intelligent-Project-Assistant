package com.assistant.message.service;

import com.assistant.message.client.SessionClient;
import com.assistant.message.client.UserClient;
import com.assistant.message.config.JwtService;
import com.assistant.message.dto.MessageRequest;
import com.assistant.message.dto.SessionDto;
import com.assistant.message.dto.UserDto;
import com.assistant.message.model.Message;
import com.assistant.message.repository.MessageRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    @Lazy
    private UserClient userClient;

    @Autowired
    @Lazy
    private SessionClient sessionClient;

    @Autowired
    private JwtService jwtService;

    public void handleIncomingMessage(MessageRequest request, String senderEmail) {
        try{
            log.info("Message Request: {}", request);
            SessionDto session = sessionClient.getSessionById(request.getSessionId());
            UserDto user = userClient.getUserByEmail(senderEmail);

            Message msg = new Message(request.getRole(),
                    request.getContent(),
                    request.getTools(),
                    LocalDateTime.now(),
                    session.getId(),
                    user.getId());
            log.info("Message object: {}", msg);
            Message savedMessage = messageRepository.save(msg);
            log.info("Saved Message: {}", savedMessage);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save message" + e);
        }
    }

    public List<Message> getMessage(String sessionId, String jwt) {
        try{
            SessionDto session = sessionClient.getSessionById(sessionId);
            Long userId = jwtService.extractUserId(jwt);

            return messageRepository.findBySessionIdAndUserId(session.getId(), userId);
        } catch (Exception e) {
            throw new RuntimeException("Cannot find message" + e);
        }
    }

    public List<Message> getMessageByEmail(String sessionId, String email) {
        try{
            SessionDto session = sessionClient.getSessionById(sessionId);
            UserDto user = userClient.getUserByEmail(email);

            return messageRepository.findBySessionIdAndUserId(session.getId(), user.getId());
        } catch (Exception e) {
            throw new RuntimeException("Cannot find message" + e);
        }
    }

    public boolean existsBySessionId(String id){
        try{
            return messageRepository.existsBySessionId(id);
        }catch (Exception e){
            throw new RuntimeException("Unable to detect session" + e);
        }
    }

    @Transactional
    public void deleteBySessionId(String id){
        try{
            messageRepository.deleteBySessionId(id);
        }catch (Exception e) {
            throw new RuntimeException("Failed to delete session" + e);
        }
    }
}
