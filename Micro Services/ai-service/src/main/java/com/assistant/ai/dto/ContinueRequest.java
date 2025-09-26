package com.assistant.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContinueRequest {
    private String runId;
    private Map<String, Object> userInputs;
    private String sessionId;
    private Long userId;
}
