package com.assistant.controller;

import com.assistant.model.AssistantMessage;
import com.assistant.service.AssistantMessageService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assistant/messages")
@PreAuthorize("isAuthenticated()")
public class AssistantMessageController {

    private final AssistantMessageService messageService;

    public AssistantMessageController(AssistantMessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    public AssistantMessage createMessage(@RequestBody AssistantMessage message) {
        return messageService.createMessage(message);
    }

    @GetMapping("/session/{sessionId}")
    public List<AssistantMessage> getMessagesBySession(@PathVariable Long sessionId) {
        return messageService.getMessagesBySessionId(sessionId);
    }

    @GetMapping("/{id}")
    public AssistantMessage getMessageById(@PathVariable Long id) throws Exception {
    	AssistantMessage message = messageService.getMessageById(id);
		if(message != null) {
			 return message;
		}
		throw new Exception("cannot find message");
    }

    @DeleteMapping("/{id}")
    public void deleteMessage(@PathVariable Long id) {
        messageService.deleteMessage(id);
    }
}
