package com.assistant.user.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;

    private String email;
    private String name;

    public UserDto(Long id, String email, String name, List<String> roles, String password) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.roles = roles;
        this.password = password;
    }

    public UserDto(String email, String name, List<String> roles) {
        this.email = email;
        this.name = name;
        this.roles = roles;
    }

    private String password;
    private List<String> roles;
}
