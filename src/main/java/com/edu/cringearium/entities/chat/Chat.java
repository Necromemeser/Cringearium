package com.edu.cringearium.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "chat", schema = "cringearium")
@Getter
@Setter
@NoArgsConstructor
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    private Long id;

    @Column(name = "chat_name", nullable = false, length = 45)
    private String chatName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToMany
    @JoinTable(
            name = "user_chat",
            schema = "cringearium",
            joinColumns = @JoinColumn(name = "chat_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> participants;
}

