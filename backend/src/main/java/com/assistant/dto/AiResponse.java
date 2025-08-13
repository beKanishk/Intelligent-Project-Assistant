package com.assistant.dto;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;

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

    public Map<String, String> getPausedMember() {
        return pausedMember;
    }

    public void setPausedMember(Map<String, String> pausedMember) {
        this.pausedMember = pausedMember;
    }

    
    // Getters and setters with proper Java naming
    public List<String> getToolUsed() {
        return toolUsed;
    }
    
    public void setToolUsed(List<String> toolUsed) {
        this.toolUsed = toolUsed;
    }
    
    public String getResponse() {
        return response;
    }
    
    public void setResponse(String response) {
        this.response = response;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public boolean isError() {
        return error;
    }
    
    public void setError(boolean error) {
        this.error = error;
    }
    
    public boolean isPaused() {
        return paused;
    }
    
    public void setPaused(boolean paused) {
        this.paused = paused;
    }
    
    public String getRunId() {
        return runId;
    }
    
    public void setRunId(String runId) {
        this.runId = runId;
    }
    
    public List<Map<String, Object>> getUserInputRequired() {
        return userInputRequired;
    }
    
    public void setUserInputRequired(List<Map<String, Object>> userInputRequired) {
        this.userInputRequired = userInputRequired;
    }
    
    public boolean isHitlAvailable() {
        return hitlAvailable;
    }
    
    public void setHitlAvailable(boolean hitlAvailable) {
        this.hitlAvailable = hitlAvailable;
    }
    
    public boolean isNeedsConfirmation() {
        return needsConfirmation;
    }
    
    public void setNeedsConfirmation(boolean needsConfirmation) {
        this.needsConfirmation = needsConfirmation;
    }
}
