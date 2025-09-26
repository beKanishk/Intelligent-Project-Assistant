package com.assistant.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AiResponse {
    @JsonProperty("tool_used")
    private List<String> toolUsed;

    private String response;
    private String content;
    private boolean error;
    private boolean paused;

    @JsonProperty("run_id")
    private String runId;

    @JsonProperty("user_input_required")
    private List<Map<String, Object>> userInputRequired;

    @JsonProperty("hitl_available")
    private boolean hitlAvailable;

    @JsonProperty("needs_confirmation")
    private boolean needsConfirmation;

    @JsonProperty("paused_member")
    private Map<String, String> pausedMember;
}
