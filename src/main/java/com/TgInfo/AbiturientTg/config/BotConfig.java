package com.TgInfo.AbiturientTg.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "bot")
@Component
@Data
public class BotConfig {
    String name;
    String token;
}
