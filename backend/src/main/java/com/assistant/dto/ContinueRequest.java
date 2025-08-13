package com.assistant.dto;

public class ContinueRequest {
    private String runId;
    private java.util.Map<String, Object> userInputs;
    private String sessionId;
    private Long userId;
    
    // Getters and setters
    public String getRunId() {
        return runId;
    }
    
    public void setRunId(String runId) {
        this.runId = runId;
    }
    
    public java.util.Map<String, Object> getUserInputs() {
        return userInputs;
    }
    
    public void setUserInputs(java.util.Map<String, Object> userInputs) {
        this.userInputs = userInputs;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
