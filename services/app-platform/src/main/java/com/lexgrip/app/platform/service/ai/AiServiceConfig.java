package com.lexgrip.app.platform.service.ai;

import dev.langchain4j.service.AiServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.langchain4j.model.chat.ChatModel;

@Configuration
public class AiServiceConfig {

    @Bean
    FlashcardAiService flashcardAiService(ChatModel openRouterChatModel) {
        return AiServices.builder(FlashcardAiService.class)
                .chatModel(openRouterChatModel)
                .build();
    }
}