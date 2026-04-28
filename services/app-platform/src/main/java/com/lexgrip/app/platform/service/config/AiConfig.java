package com.lexgrip.app.platform.service.config;


import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class AiConfig {
    @Bean
    ChatModel openRouterChatModel(
    ) {
        String apiKey = "";

        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .baseUrl("https://openrouter.ai/api/v1")
                .modelName("qwen/qwen3-235b-a22b-2507").temperature(0.3)
                .customHeaders(Map.of(
                        "HTTP-Referer", "https://lexgrip.com",
                        "X-Title", "LexGrip"
                ))
                .logRequests(true)
                .logResponses(true)
                .build();
    }
}
