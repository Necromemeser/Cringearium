package com.edu.cringearium.entities.chat;


import com.edu.cringearium.entities.user.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;

    @Lob
    @Column(name = "content", nullable = false)
    private byte[] content;

    @Column(name = "timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @ManyToOne
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @Column(name = "is_ai_response", nullable = false)
    private boolean isAiResponse = false;

    // Конструкторы
    public Message() {}

    public Message(byte[] content, User user, Chat chat, boolean isAiResponse) {
        this.content = content;
        this.user = user;
        this.chat = chat;
        this.isAiResponse = isAiResponse;
        this.timestamp = LocalDateTime.now();
    }


    public Long getId() { return id; }
    public byte[] getContent() { return content; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public User getUser() { return user; }
    public Chat getChat() { return chat; }
    public boolean isAiResponse() { return isAiResponse; }

    public void setId(Long id) { this.id = id; }
    public void setContent(byte[] content) { this.content = content; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public void setUser(User user) { this.user = user; }
    public void setChat(Chat chat) { this.chat = chat; }
    public void setAiResponse(boolean aiResponse) { isAiResponse = aiResponse; }
}
