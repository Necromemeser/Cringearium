package com.edu.cringearium.controllers.api.AI;


import com.edu.cringearium.entities.chat.Message;
import com.edu.cringearium.services.MessageService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class OllamaApiController {

    private final ChatClient chatClient;
    private final MessageService messageService;

    public OllamaApiController(ChatClient chatClient, MessageService messageService) {
        this.chatClient = chatClient;
        this.messageService = messageService;
    }

    @PostMapping("/api/ollama")
    public Flux<String> ollama(@RequestParam Long chatId, @RequestParam String input) {
        // Сохранение пользовательского сообщения
        messageService.saveUserMessage(chatId, input);

        // Получаем историю сообщений чата
        List<Message> messages = messageService.getMessagesByChat(chatId);

        // Создаем список для форматированной истории
        List<String> formattedHistory = new ArrayList<>();

        // Если есть история сообщений, обрабатываем ее
        if (!messages.isEmpty()) {
            formattedHistory = messages.stream()
                    .map(msg -> {
                        String content = new String(msg.getContent(), StandardCharsets.UTF_8);
                        return msg.isAiResponse()
                                ? "Ответ: " + content
                                : "Сообщение пользователя: " + content;
                    })
                    .collect(Collectors.toList());
        }

        // Создаём поток ответа от AI
        Flux<String> responseStream = chatClient.prompt()
                .user(String.join("\n", formattedHistory)) // Объединяем историю в одну строку
                .stream()
                .content()
                .share(); // Дублируем поток для сохранения и передачи клиенту

        System.out.println(String.join("\n", formattedHistory));

        // Сохранение полного ответа в бд
        responseStream.collectList()
                .map(responseList -> String.join("", responseList))
                .doOnSuccess(fullResponse -> messageService.saveAiResponse(chatId, fullResponse))
                .subscribe(); // Запускаем сбор полного ответа в фоновом режиме

        return responseStream; // Отправляем поток клиенту
    }

    @GetMapping("/api/ollama")
    public Flux<String> ollama(@RequestParam String input) {
        return chatClient.prompt()
                .user(input)
                .stream()
                .content();
    }
}
