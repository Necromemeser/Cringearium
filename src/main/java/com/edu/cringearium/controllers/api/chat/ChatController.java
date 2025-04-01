package com.edu.cringearium.controllers.api.chat;

import com.edu.cringearium.config.security.CustomUserDetails;
import com.edu.cringearium.dto.ChatDTO;
import com.edu.cringearium.entities.user.User;
import com.edu.cringearium.entities.chat.Chat;
import com.edu.cringearium.entities.chat.Message;
import com.edu.cringearium.repositories.chat.ChatRepository;
import com.edu.cringearium.services.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chats")
public class ChatController {

    private final ChatRepository chatRepository;
    private final MessageService messageService;

    public ChatController(ChatRepository chatRepository, MessageService messageService) {
        this.chatRepository = chatRepository;
        this.messageService = messageService;
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getUser();
    }

    // Получение списка чатов текущего пользователя
    @GetMapping
    public List<ChatDTO> getAllChats() {
        User currentUser = getCurrentUser();
        return chatRepository.findByParticipantsContaining(currentUser)
                .stream()
                .map(chat -> new ChatDTO(chat.getId(), chat.getChatName(), chat.getCreatedAt()))
                .collect(Collectors.toList());
    }

    // Получение чата по ID (только если пользователь является участником)
    @GetMapping("/{id}")
    public ResponseEntity<ChatDTO> getChatById(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        Optional<Chat> chat = chatRepository.findById(id);

        if (chat.isPresent() && chat.get().getParticipants().stream()
                .anyMatch(participant -> participant.getId().equals(currentUser.getId()))) {
            Chat chatEntity = chat.get();
            return ResponseEntity.ok(new ChatDTO(chatEntity.getId(), chatEntity.getChatName(), chatEntity.getCreatedAt()));
        }
        return ResponseEntity.notFound().build();
    }

    // Создание нового чата
    @PostMapping
    public ChatDTO createChat(@RequestBody Chat chat) {
        User currentUser = getCurrentUser();
        chat.setParticipants(Set.of(currentUser));
        Chat savedChat = chatRepository.save(chat);
        return new ChatDTO(savedChat.getId(), savedChat.getChatName(), savedChat.getCreatedAt());
    }

    // Удаление чата (только если пользователь является участником)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChat(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        Optional<Chat> chat = chatRepository.findById(id);
        if (chat.isPresent() && chat.get().getParticipants().stream()
                .anyMatch(participant -> participant.getId().equals(currentUser.getId()))) {
            chatRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Получение всех сообщений в чате (только если пользователь является участником)
    @GetMapping("/{chatId}/messages")
    public ResponseEntity<List<Map<String, Object>>> getChatMessages(@PathVariable Long chatId) {
        try {
            User currentUser = getCurrentUser();
            List<Message> messages = messageService.getMessagesByChat(chatId);
            
            List<Map<String, Object>> response = messages.stream().map(msg -> {
                Map<String, Object> messageData = new HashMap<>();
                messageData.put("id", msg.getId());
                messageData.put("content", new String(msg.getContent(), StandardCharsets.UTF_8));
                messageData.put("isAiResponse", msg.isAiResponse());
                messageData.put("timestamp", msg.getTimestamp());
                messageData.put("userId", msg.getUser() != null ? msg.getUser().getId() : null);
                return messageData;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("не найден")) {
                return ResponseEntity.notFound().build();
            } else if (e.getMessage().contains("не является участником")) {
                return ResponseEntity.status(403).build();
            }
            return ResponseEntity.internalServerError().build();
        }
    }
}
