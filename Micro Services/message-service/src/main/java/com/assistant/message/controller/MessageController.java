package com.assistant.message.controller;

import com.assistant.message.dto.MessageRequest;
import com.assistant.message.model.Message;
import com.assistant.message.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping("/history/{sessionId}")
    public ResponseEntity<List<Message>> getMessageHistoryByPath(
            @PathVariable String sessionId,
            Principal principal) {
        String email = principal.getName();
        List<Message> messages = messageService.getMessageByEmail(sessionId, email);
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    @PostMapping("/save/{senderEmail}")
    public ResponseEntity<?> handleIncomingMessage(@RequestBody MessageRequest request, @PathVariable String senderEmail){
        try{
            messageService.handleIncomingMessage(request, senderEmail);
            return new ResponseEntity<>("Message saved successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to save message", HttpStatus.OK);
        }
    }

    @PostMapping("/exists/{sessionId}")
    public boolean existsBySessionId(@PathVariable String sessionId){
        return messageService.existsBySessionId(sessionId);
    }

    @PostMapping("/delete/{sessionId}")
    public void deleteBySessionId(@PathVariable String sessionId){
        messageService.deleteBySessionId(sessionId);
    }

}
