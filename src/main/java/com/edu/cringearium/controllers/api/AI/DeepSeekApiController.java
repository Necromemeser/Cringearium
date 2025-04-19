package com.edu.cringearium.controllers.api.AI;

import com.edu.cringearium.entities.chat.Message;
import com.edu.cringearium.services.MessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
public class DeepSeekApiController {

    private final WebClient deepseekClient; // или RestClient
    private final MessageService messageService;

    public DeepSeekApiController(WebClient deepseekClient, MessageService messageService) {
        this.deepseekClient = deepseekClient;
        this.messageService = messageService;
    }

    @PostMapping("/api/deepseek")
    public Flux<String> deepseek(@RequestParam Long chatId, @RequestParam String input) {
        messageService.saveUserMessage(chatId, input);
        List<Message> messages = messageService.getMessagesByChat(chatId);

        List<Map<String, String>> messageHistory = new ArrayList<>();
        messageHistory.add(Map.of(
                "role", "system",
                "content", "Ты — дружелюбный и лаконичный ассистент образовательной платформы по имени Кринжик. " +
                        "Помогай студентам, объясняй сложное простым языком, и отвечай чётко по существу. " +
                        "Отвечай только на русском языке."
        ));

        for (Message msg : messages) {
            String content = new String(msg.getContent(), StandardCharsets.UTF_8);
            messageHistory.add(Map.of(
                    "role", msg.isAiResponse() ? "assistant" : "user",
                    "content", content
            ));
        }

        Map<String, Object> requestBody = Map.of(
                "model", "deepseek-chat",
                "messages", messageHistory,
                "stream", true
        );

        Flux<String> responseStream = deepseekClient.post()
                .bodyValue(requestBody)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(String.class)
                .map(String::trim)
                .doOnNext(raw -> System.out.println("Raw chunk: " + raw))
                .filter(chunk -> !chunk.equals("[DONE]"))
                .map(this::parseChunkContent)
                .filter(Objects::nonNull)
                .share();

        ConnectableFlux<String> connectableFlux = responseStream.publish();

        connectableFlux
                .collectList()
                .map(chunks -> String.join("", chunks))
                .doOnSuccess(fullResponse -> {
                    messageService.saveAiResponse(chatId, fullResponse);
                    System.out.println("Сохранили полный ответ: " + fullResponse);
                })
                .subscribe();

        connectableFlux.connect(); // ОБЯЗАТЕЛЬНО!

        return connectableFlux;
    }


    // Метод для парсинга JSON и извлечения текста
    private String parseChunkContent(String jsonChunk) {
        System.out.println("Парсим джейсончик");
        try {
            JsonNode node = new ObjectMapper().readTree(jsonChunk);
            JsonNode choices = node.path("choices");
            if (choices.isArray() && choices.size() > 0) {
                JsonNode delta = choices.get(0).path("delta");
                if (delta.has("content")) {
                    return delta.get("content").asText();
                }
            }
            return null;
        } catch (JsonProcessingException e) {
            System.err.println("Ошибка парсинга чанка: " + jsonChunk);
            return null;
        }
    }
}
