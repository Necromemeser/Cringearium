package com.edu.cringearium.controllers.AI;

import com.edu.cringearium.services.MessageService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
class OllamaApiController {

    private final ChatClient chatClient;
    private final MessageService messageService;

    public OllamaApiController(ChatClient chatClient, MessageService messageService) {
        this.chatClient = chatClient;
        this.messageService = messageService;
    }

    @PostMapping("/ollama")
    public Flux<String> ollama(@RequestParam Long chatId, @RequestParam String input) {
        // Сохранение пользовательского сообщения
        messageService.saveUserMessage(chatId, input, null);

        // Создаём поток ответа от AI
        Flux<String> responseStream = chatClient.prompt()
                .user(input)
                .stream()
                .content()
                .share(); // Дублируем поток для сохранения и передачи клиенту

        // Сохранение полного ответа в бд
        responseStream.collectList()
                .map(responseList -> String.join("", responseList))
                .doOnSuccess(fullResponse -> messageService.saveAiResponse(chatId, fullResponse))
                .subscribe(); // Запускаем сбор полного ответа в фоновом режиме

        return responseStream; // Отправляем поток клиенту
    }

    @GetMapping("/ollama")
    public Flux<String> ollama(@RequestParam String input) {
        return chatClient.prompt()
                .user(input)
                .stream()
                .content();
    }
}
