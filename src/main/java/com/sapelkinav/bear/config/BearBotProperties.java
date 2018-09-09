package com.sapelkinav.bear.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("bot")
public class BearBotProperties {
    String botName;
    String token;
}
