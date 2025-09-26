package com.assistant.session.controller;

import com.assistant.session.model.Session;
import com.assistant.session.service.SessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping
    private String response(){
        return "Session Service";
    }

    @PostMapping
    public ResponseEntity<String> createSession(@RequestHeader("Authorization") String jwt) {
        log.info("Creating session");
        jwt = jwt.substring(7);

        String sessionId = sessionService.createSession(jwt);
        log.info("Session id: {}", sessionId);
        return new ResponseEntity<>(sessionId, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public List<Session> getSessionsByUser(@PathVariable Long userId) {
        log.info("Getting sessions by user id");
        try{
            return sessionService.getSessionsByUserId(userId);
        }catch (Exception e){
            throw new RuntimeException("Cannot find session." + e.getStackTrace());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Session> getSessionById(@PathVariable String id) {
        log.info("Getting sessions by session id");
        Session session = sessionService.getSessionById(id);
        return new ResponseEntity<>(session, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSession(@PathVariable String id) {
        log.info("Delete session" + id);
        try{
            sessionService.deleteSession(id);
            return new ResponseEntity<>("Session deleted", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete session", HttpStatus.OK);
        }

    }
}

