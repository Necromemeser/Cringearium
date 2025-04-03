package com.edu.cringearium.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "app.yookassa")
@Data
public class YooKassaConfig {
    private String apiUrl;
    private String authToken;
    private String returnUrl;
    private String initialMessage;
}
