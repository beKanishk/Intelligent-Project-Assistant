package com.assistant.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class Response {
    private Long id;
    private String email;
    private String name;
    private List<String> role;

    public Response(Long id, String email, String name, List<String> role) {
        super();
        this.id = id;
        this.email = email;
        this.name = name;
        this.role = role;
    }
}

