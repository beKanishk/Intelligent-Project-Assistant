package com.assistant.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String role; // user, assistant, or tool

    @Column(columnDefinition = "TEXT")
    private String content;
    
    @ElementCollection
    @CollectionTable(name = "message_tools", joinColumns = @JoinColumn(name = "message_id"))
    @Column(name = "tool_name")
    private List<String> tools;
//
//    @Column(columnDefinition = "TEXT")
//    private String toolArguments;

//    @Column(columnDefinition = "TEXT")
//    private String toolOutput;

    public Message(String role, String content, List<String> tools, 
			LocalDateTime timestamp, Session session, User user) {
		super();
		this.role = role;
		this.content = content;
		this.tools = tools;
//		this.toolArguments = toolArguments;
//		this.toolOutput = toolOutput;
		this.timestamp = timestamp;
//		this.metadata = metadata;
		this.session = session;
		this.user = user;
	}

	private LocalDateTime timestamp;

//    @Column(columnDefinition = "TEXT")
//    private String metadata; // optional JSON string (e.g., token usage)

    // 🔗 Session association (many messages per session)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private Session session;

    // 🔗 User association (message belongs to a user)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Message() {
        this.timestamp = LocalDateTime.now();
    }

    // Getters and setters
    // (Include all fields or use Lombok if preferred)
    
    public Long getId() {
        return id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


//    public String getToolArguments() {
//        return toolArguments;
//    }
//
//    public void setToolArguments(String toolArguments) {
//        this.toolArguments = toolArguments;
//    }

//    public String getToolOutput() {
//        return toolOutput;
//    }
//
//    public void setToolOutput(String toolOutput) {
//        this.toolOutput = toolOutput;
//    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

//    public String getMetadata() {
//        return metadata;
//    }
//
//    public void setMetadata(String metadata) {
//        this.metadata = metadata;
//    }

	public List<String> getTools() {
		return tools;
	}

	public void setTools(List<String> tools) {
		this.tools = tools;
	}
}
