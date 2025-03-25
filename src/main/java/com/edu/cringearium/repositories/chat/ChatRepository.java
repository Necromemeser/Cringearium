package com.edu.cringearium.repositories.chat;

import com.edu.cringearium.entities.user.User;
import com.edu.cringearium.entities.chat.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findByParticipantsContaining(User user);
}

