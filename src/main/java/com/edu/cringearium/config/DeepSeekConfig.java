package com.edu.cringearium.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class DeepSeekConfig {
    @Bean
    public WebClient deepseekWebClient(@Value("${deepseek.api.url}") String apiUrl,
                                       @Value("${deepseek.api.key}") String apiKey) {
        return WebClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
