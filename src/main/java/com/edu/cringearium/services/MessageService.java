package com.edu.cringearium.services;

import com.edu.cringearium.config.security.CustomUserDetails;
import com.edu.cringearium.entities.User;
import com.edu.cringearium.entities.chat.Chat;
import com.edu.cringearium.entities.chat.Message;
import com.edu.cringearium.repositories.UserRepository;
import com.edu.cringearium.repositories.chat.ChatRepository;
import com.edu.cringearium.repositories.chat.MessageRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    public MessageService(MessageRepository messageRepository, ChatRepository chatRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getUser();
    }

    // Сохранение пользовательского сообщения
    public Message saveUserMessage(Long chatId, String content) {
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new RuntimeException("Чат не найден"));
        User currentUser = getCurrentUser();
        
        // Проверяем, является ли пользователь участником чата
        boolean isParticipant = chat.getParticipants().stream()
                .anyMatch(user -> user.getId().equals(currentUser.getId()));
        if (!isParticipant) {
            throw new RuntimeException("Пользователь не является участником чата");
        }

        Message message = new Message(content.getBytes(StandardCharsets.UTF_8), currentUser, chat, false);
        return messageRepository.save(message);
    }

    // Сохранение ответа ИИ
    public Message saveAiResponse(Long chatId, String content) {
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new RuntimeException("Чат не найден"));
        Message message = new Message(content.getBytes(StandardCharsets.UTF_8), null, chat, true);
        return messageRepository.save(message);
    }

    // Получение сообщений конкретного чата
    @Transactional
    public List<Message> getMessagesByChat(Long chatId) {
        User currentUser = getCurrentUser();
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new RuntimeException("Чат не найден"));
        
        // Проверяем, является ли пользователь участником чата
        boolean isParticipant = chat.getParticipants().stream()
                .anyMatch(user -> user.getId().equals(currentUser.getId()));
        if (!isParticipant) {
            throw new RuntimeException("Пользователь не является участником чата");
        }

        return messageRepository.findByChatIdOrderByTimestampAsc(chatId);
    }
}

