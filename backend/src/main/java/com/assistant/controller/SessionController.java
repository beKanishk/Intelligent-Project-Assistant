package com.assistant.controller;

import com.assistant.model.Session;
import com.assistant.service.SessionService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
@PreAuthorize("isAuthenticated()")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping
    public ResponseEntity<String> createSession(@RequestHeader("Authorization") String jwt) {
        String sessionId = sessionService.createSession(jwt);
        return new ResponseEntity<>(sessionId, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public List<Session> getSessionsByUser(@PathVariable Long userId) {
        return sessionService.getSessionsByUserId(userId);
    }

    @GetMapping("/{id}")
    public Session getSessionById(@PathVariable Long id) {
        return sessionService.getSessionById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteSession(@PathVariable Long id) {
        sessionService.deleteSession(id);
    }
}
