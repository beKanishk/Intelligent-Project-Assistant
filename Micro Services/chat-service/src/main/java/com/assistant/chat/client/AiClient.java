package com.assistant.chat.client;

import com.assistant.chat.config.FeignConfig;
import com.assistant.chat.dto.AiResponse;
import com.assistant.chat.dto.ContinueRequest;
import com.assistant.chat.dto.MessageRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "AI-SERVICE", configuration = FeignConfig.class)
public interface AiClient {
    @PostMapping("/api/ai//call-agent")
    AiResponse callAgent(@RequestBody MessageRequest msgRequest);

    @PostMapping("/api/ai//continue-agent")
    AiResponse continueAgent(@RequestBody ContinueRequest continueRequest);
}
