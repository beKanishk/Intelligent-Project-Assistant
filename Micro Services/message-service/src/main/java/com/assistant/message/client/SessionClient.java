package com.assistant.message.client;

import com.assistant.message.config.FeignConfig;
import com.assistant.message.dto.SessionDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "SESSION-SERVICE", configuration = FeignConfig.class)
public interface SessionClient {

    @GetMapping("/api/sessions/{id}")
    SessionDto getSessionById(@PathVariable String id);
}
