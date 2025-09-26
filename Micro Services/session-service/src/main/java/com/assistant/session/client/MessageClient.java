package com.assistant.session.client;

import com.assistant.session.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "MESSAGE-SERVICE", configuration = FeignConfig.class)
public interface MessageClient {
    @PostMapping("/api/message/exists/{sessionId}")
    public boolean existsBySessionId(@PathVariable String sessionId);

    @PostMapping("/api/message/delete/{sessionId}")
    public void deleteBySessionId(@PathVariable String sessionId);
}
