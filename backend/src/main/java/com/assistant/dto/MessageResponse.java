package com.assistant.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageResponse {
    private String id;
    private String type; // "user", "agent", "error"
    private String content;
    
    @JsonProperty("session_id")
    private String sessionId;
    
    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public String getRunId() {
		return runId;
	}

	public void setRunId(String runId) {
		this.runId = runId;
	}

	public List<String> getToolUsed() {
		return toolUsed;
	}

	public void setToolUsed(List<String> toolUsed) {
		this.toolUsed = toolUsed;
	}

	public boolean isPaused() {
		return paused;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	public boolean isUserInputRequired() {
		return userInputRequired;
	}

	public void setUserInputRequired(boolean userInputRequired) {
		this.userInputRequired = userInputRequired;
	}

	public List<UserInputField> getUserInputFields() {
		return userInputFields;
	}

	public void setUserInputFields(List<UserInputField> userInputFields) {
		this.userInputFields = userInputFields;
	}

	public boolean isNeedsConfirmation() {
		return needsConfirmation;
	}

	public void setNeedsConfirmation(boolean needsConfirmation) {
		this.needsConfirmation = needsConfirmation;
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public boolean isHitlAvailable() {
		return hitlAvailable;
	}

	public void setHitlAvailable(boolean hitlAvailable) {
		this.hitlAvailable = hitlAvailable;
	}

	public java.util.Map<String, String> getPausedMember() {
		return pausedMember;
	}

	public void setPausedMember(java.util.Map<String, String> pausedMember) {
		this.pausedMember = pausedMember;
	}

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
