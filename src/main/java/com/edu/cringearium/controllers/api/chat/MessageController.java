package com.edu.cringearium.controllers.api.chat;


import com.edu.cringearium.entities.chat.Chat;
import com.edu.cringearium.entities.chat.Message;
import com.edu.cringearium.entities.user.User;
import com.edu.cringearium.repositories.chat.ChatRepository;
import com.edu.cringearium.repositories.chat.MessageRepository;
import com.edu.cringearium.repositories.user.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;

    public MessageController(MessageRepository messageRepository, UserRepository userRepository, ChatRepository chatRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
    }

    // Получить все сообщения чата
    @GetMapping("/chat/{chatId}")
    public ResponseEntity<List<Message>> getMessagesByChat(@PathVariable Long chatId) {
        List<Message> messages = messageRepository.findByChatId(chatId);
        return ResponseEntity.ok(messages);
    }

    // Отправить сообщение
    @PostMapping("/send")
    public ResponseEntity<Message> sendMessage(
            @RequestParam(required = false) Long userId,
            @RequestParam Long chatId,
            @RequestBody byte[] content,
            @RequestParam(required = false, defaultValue = "false") boolean isAiResponse) {


        if (!isAiResponse) {
            if (userId == null) {
                return ResponseEntity.badRequest().build();
            }
            if (userRepository.findById(userId).isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
        }

        Optional<Chat> chatOpt = chatRepository.findById(chatId);

        if (chatOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        User user = null;
        if (!isAiResponse) {
            user = userRepository.findById(userId).orElse(null);
        }

        // Создаем новое сообщение
        Message message = new Message(content, user, chatOpt.get(), isAiResponse);
        Message savedMessage = messageRepository.save(message);
        return ResponseEntity.ok(savedMessage);
    }
}
