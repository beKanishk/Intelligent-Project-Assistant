package com.assistant.chat.client;

import com.assistant.chat.config.FeignConfig;
import com.assistant.chat.dto.MessageRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "MESSAGE-SERVICE", configuration = FeignConfig.class)
public interface MessageClient {
    @PostMapping("/api/message/save/{senderEmail}")
    void handleIncomingMessage(@RequestBody MessageRequest request, @PathVariable String senderEmail);
}
