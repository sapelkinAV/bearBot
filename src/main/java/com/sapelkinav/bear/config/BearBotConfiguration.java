package com.sapelkinav.bear.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.generics.BotOptions;

@Data
@Configuration
@ConfigurationProperties(prefix = "bot")
public class BearBotConfiguration {
    private String botName;
    private String token;
    private String baseUrl;

}
