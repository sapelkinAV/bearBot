package com.sapelkinav.masturbear.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Data
@Configuration
@ConfigurationProperties("bot")
public class BearBotProperties {
    String botName;
    String token;
}
