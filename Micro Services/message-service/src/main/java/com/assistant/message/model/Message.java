package com.assistant.message.model;

import com.assistant.message.dto.SessionDto;
import com.assistant.message.dto.UserDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "messages")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String role;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime timestamp;

    private Long userId;    // reference user
    private String sessionId; // reference session

    @ElementCollection
    private List<String> tools;

    public Message(String role, String content, List<String> tools, LocalDateTime timestamp, String sessionId, Long userId) {
        this.role = role;
        this.content = content;
        this.tools = tools;
        this.timestamp = timestamp;
        this.sessionId = sessionId;
        this.userId = userId;
    }

}

