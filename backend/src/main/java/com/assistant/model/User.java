package com.assistant.model;


import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
    private String email;
    private String name;
    private String password;
    private String role;
    
    @OneToMany(mappedBy = "user")
    private List<AssistantSession> sessions;

    @OneToMany(mappedBy = "user")
    private List<Message> messages;

    public List<AssistantSession> getSessions() {
		return sessions;
	}

	public void setSessions(List<AssistantSession> sessions) {
		this.sessions = sessions;
	}

	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}

	public User() {}

    public User(String email, String password, String role, String name) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.role = role;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
