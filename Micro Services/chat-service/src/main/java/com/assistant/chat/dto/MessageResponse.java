package com.assistant.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse {
    private String id;
    private String type; // "user", "agent", "error"
    private String content;

    @JsonProperty("session_id")
    private String sessionId;

    private Long timestamp;

    @JsonProperty("run_id")
    private String runId;

    @JsonProperty("tool_used")
    private List<String> toolUsed;

    private boolean paused = false;

    @JsonProperty("user_input_required")
    private boolean userInputRequired = false;

    @JsonProperty("user_input_fields")
    private List<UserInputField> userInputFields;

    @JsonProperty("needs_confirmation")
    private boolean needsConfirmation = false;

    private boolean error = false;

    // Additional metadata
    @JsonProperty("hitl_available")
    private boolean hitlAvailable = false;

    @JsonProperty("paused_member")
    private java.util.Map<String, String> pausedMember;
}
