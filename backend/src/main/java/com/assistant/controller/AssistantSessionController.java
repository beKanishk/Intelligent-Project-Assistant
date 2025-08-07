package com.assistant.controller;

import com.assistant.model.AssistantSession;
import com.assistant.service.AssistantSessionService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assistant/sessions")
@PreAuthorize("isAuthenticated()")
public class AssistantSessionController {

    private final AssistantSessionService sessionService;

    public AssistantSessionController(AssistantSessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping
    public AssistantSession createSession(@RequestBody AssistantSession session) {
        return sessionService.createSession(session);
    }

    @GetMapping("/user/{userId}")
    public List<AssistantSession> getSessionsByUser(@PathVariable Long userId) {
        return sessionService.getSessionsByUserId(userId);
    }

    @GetMapping("/{id}")
    public AssistantSession getSessionById(@PathVariable Long id) {
        return sessionService.getSessionById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteSession(@PathVariable Long id) {
        sessionService.deleteSession(id);
    }
}
