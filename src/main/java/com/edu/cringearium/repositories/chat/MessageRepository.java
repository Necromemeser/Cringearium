package com.edu.cringearium.repositories.chat;


import com.edu.cringearium.entities.chat.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChatId(Long chatId);
    List<Message> findByChatIdOrderByTimestampAsc(Long chatId);
}

