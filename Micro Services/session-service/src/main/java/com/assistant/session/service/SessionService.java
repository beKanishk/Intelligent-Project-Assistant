package com.assistant.session.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.assistant.session.client.MessageClient;
import com.assistant.session.client.UserClient;
import com.assistant.session.config.JwtService;
import com.assistant.session.model.Session;
import com.assistant.session.repository.SessionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;


import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Slf4j
@Service
public class SessionService {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    @Lazy
    private UserClient userClient;

    @Autowired
    @Lazy
    private MessageClient messageClient;

    public String createSession(String jwt) {
        String session_id = UUID.randomUUID().toString();
        Long userId = jwtService.extractUserId(jwt);

        Session session = new Session();
        session.setUser(userId);
        session.setId(session_id);
        sessionRepository.save(session);

        return session_id;
    }

    public Session getSessionById(String id) {
        return sessionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Session not found with ID: " + id));
    }

    @Transactional
    public void deleteSession(String id) {
        if (!sessionRepository.existsById(id)) {
            throw new EntityNotFoundException("Session not found with ID: " + id);
        }

        try{
            Optional<Session> session = sessionRepository.findById(id);

            if(messageClient.existsBySessionId(session.get().getId())) {
                messageClient.deleteBySessionId(id);
            }

            sessionRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete session" + e);
        }

    }

    public List<Session> getSessionsByUserId(Long userId) {
        try{
            log.info("Service method called");
            List<Session> session = sessionRepository.findByUser(userId);
            return session;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}

