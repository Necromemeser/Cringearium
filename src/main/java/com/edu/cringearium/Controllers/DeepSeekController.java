package com.edu.cringearium.Controllers;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class DeepSeekController {

    private final ChatClient chatClient;

    public DeepSeekController(@Qualifier("openAiChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/deepseek")
    public String deepseek() {
        return chatClient.prompt()
                .user("How many r's are in Strawberry")
                .call()
                .content();
    }

    @GetMapping("/deepseek-stream")
    public Flux<String> deepseekStream() {
        return chatClient.prompt()
                .user("How many r's are in Strawberry")
                .stream()
                .content();
    }

}
