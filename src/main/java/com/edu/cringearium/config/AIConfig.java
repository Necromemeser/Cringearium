package com.edu.cringearium.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AIConfig {

    @Bean
    public ChatClient ollamaChatClient(@Qualifier("ollamaChatModel") OllamaChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultSystem("Ты — дружелюбный и лаконичный ассистент образовательной платформы" +
                        "по имени Кринжик." +
                        " Помогай студентам, объясняй сложное простым языком, и отвечай чётко по существу." +
                        " Отвечай только на русском языке.")
                .build();
    }
}

//    @Bean
//    public ChatClient ollamaChatClient(OllamaChatModel chatModel) {
//        return ChatClient.create(chatModel);
//    }
//}


