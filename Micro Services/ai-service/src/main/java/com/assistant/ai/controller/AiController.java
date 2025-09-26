package com.assistant.ai.controller;

import com.assistant.ai.dto.AiResponse;
import com.assistant.ai.dto.ContinueRequest;
import com.assistant.ai.dto.MessageRequest;
import com.assistant.ai.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Autowired
    private AiService aiService;

    @PostMapping("/call-agent")
    public ResponseEntity<?> callAgent(@RequestBody MessageRequest msgRequest){
        try{
            AiResponse aiResponse = aiService.callAgent(msgRequest);
            return new ResponseEntity<>(aiResponse, HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException("Error while calling agent" + e);
        }
    }

    @PostMapping("/continue-agent")
    public ResponseEntity<?> continueAgent(@RequestBody ContinueRequest continueRequest){
        try{
            AiResponse aiResponse = aiService.continueAgent(continueRequest);
            return new ResponseEntity<>(aiResponse, HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException("Error while calling continue agent request" + e);
        }

    }
}
