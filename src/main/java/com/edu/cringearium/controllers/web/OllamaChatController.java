package com.edu.cringearium.controllers.web;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class OllamaChatController {

    private final ChatClient chatClient;

    public OllamaChatController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/chat")
    public String index() {
        return "aichat"; // Возвращает имя шаблона Thymeleaf
    }
}

