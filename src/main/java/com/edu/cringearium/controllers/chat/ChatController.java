package com.edu.cringearium.controllers.chat;

import com.edu.cringearium.entities.chat.Chat;
import com.edu.cringearium.entities.chat.Message;
import com.edu.cringearium.repositories.chat.ChatRepository;
import com.edu.cringearium.services.MessageService;
import org.springframework.http.ResponseEntity;
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

    // Получение списка всех чатов
    @GetMapping
    public List<Chat> getAllChats() {
        return chatRepository.findAll();
    }

    // Получение чата по ID
    @GetMapping("/{id}")
    public ResponseEntity<Chat> getChatById(@PathVariable Long id) {
        Optional<Chat> chat = chatRepository.findById(id);
        return chat.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Создание нового чата
    @PostMapping
    public Chat createChat(@RequestBody Chat chat) {
        return chatRepository.save(chat);
    }

    // Удаление чата
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChat(@PathVariable Long id) {
        if (chatRepository.existsById(id)) {
            chatRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Получение всех сообщений в чате
    @GetMapping("/{chatId}/messages")
    public ResponseEntity<List<Map<String, Object>>> getChatMessages(@PathVariable Long chatId) {
        List<Message> messages = messageService.getMessagesByChat(chatId);
        List<Map<String, Object>> response = messages.stream().map(msg -> {
            Map<String, Object> messageData = new HashMap<>();
            messageData.put("id", msg.getId());
            messageData.put("content", new String(msg.getContent(), StandardCharsets.UTF_8));
            messageData.put("isAiResponse", msg.isAiResponse());
            messageData.put("timestamp", msg.getTimestamp());
            return messageData;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}
