package com.assistant.message.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionDto {
    private String id;
    private Long user;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
