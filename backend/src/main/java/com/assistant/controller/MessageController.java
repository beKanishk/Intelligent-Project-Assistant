package com.assistant.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.assistant.model.Message;
import com.assistant.service.MessageService;

@RestController
@RequestMapping("/api/message")

public class MessageController {
	@Autowired
	private MessageService messageService;

    @GetMapping
    private String response(){
        return "Message Service";
    }

	@GetMapping("/history/{sessionId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Message>> getMessageHistoryByPath(
            @PathVariable String sessionId, 
            Principal principal) {
        String email = principal.getName();
        List<Message> messages = messageService.getMessageByEmail(sessionId, email);
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }
}
