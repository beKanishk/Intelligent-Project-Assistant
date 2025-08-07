package com.assistant.controller;

import com.assistant.model.AssistantMessage;

public class MessageDto {
	 private String content;
	    private String preferredTool;
	    private String pluginUsed;
	    private String aiResponse;
	    
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		public String getPreferredTool() {
			return preferredTool;
		}
		public void setPreferredTool(String preferredTool) {
			this.preferredTool = preferredTool;
		}
		public String getPluginUsed() {
			return pluginUsed;
		}
		public void setPluginUsed(String pluginUsed) {
			this.pluginUsed = pluginUsed;
		}
		public String getAiResponse() {
			return aiResponse;
		}
		public void setAiResponse(String aiResponse) {
			this.aiResponse = aiResponse;
		}

//	    public AssistantMessage toEntity() {
//	        return AssistantMessage.builder()
//	                .content(content)
//	                .preferredTool(preferredTool)
//	                .pluginUsed(pluginUsed)
//	                .aiResponse(aiResponse)
//	                .build();
//	    }

//	    public static MessageDto fromEntity(AssistantMessage message) {
//	        return MessageDto.builder()
//	                .content(message.getContent())
//	                .preferredTool(message.getPreferredTool())
//	                .pluginUsed(message.getPluginUsed())
//	                .aiResponse(message.getAiResponse())
//	                .build();
//	    }
	
	
}
