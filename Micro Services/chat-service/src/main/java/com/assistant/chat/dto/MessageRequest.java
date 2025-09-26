package com.assistant.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageRequest {
    private String content;
    private String sessionId;
    private List<String> tools;
    private Long userId;
    private String role;
    private boolean paused;
    private boolean needsConfirmation;
}
