package com.edu.cringearium.dto;

import java.time.LocalDateTime;

public class ChatDTO {
    private Long id;
    private String chatName;
    private LocalDateTime createdAt;

    public ChatDTO(Long id, String chatName, LocalDateTime createdAt) {
        this.id = id;
        this.chatName = chatName;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
} 