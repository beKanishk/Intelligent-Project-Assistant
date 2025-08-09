package com.assistant.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "assistant_sessions")
public class Session {

    @Id
    private String id;

//    // 🔗 One session has many messages
//    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
//    private List<Message> messages;

    // 🔗 Session belongs to a user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Session() {
    }

    // Getters and setters

    
//
//    public List<Message> getMessages() {
//        return messages;
//    }
//
//    public void setMessages(List<Message> messages) {
//        this.messages = messages;
//    }

    public User getUser() {
        return user;
    }

    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setUser(User user) {
        this.user = user;
    }
}
