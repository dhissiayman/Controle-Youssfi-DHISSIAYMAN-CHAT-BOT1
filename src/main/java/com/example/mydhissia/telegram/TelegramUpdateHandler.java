package com.example.mydhissia.telegram;

import com.example.mydhissia.ai.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class TelegramUpdateHandler {

    private static final Logger log = LoggerFactory.getLogger(TelegramUpdateHandler.class);

    private final ChatService chatService;

    public TelegramUpdateHandler(ChatService chatService) {
        this.chatService = chatService;
    }

    public String handleUpdate(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
            String userText = update.getMessage().getText();

            log.info("Telegram message received. chatId={}, text='{}'", chatId, userText);

            String response;
            if (userText.startsWith("/rag") || userText.startsWith("/kb")) {
                String query = userText.replaceFirst("^/(rag|kb)\\s*", "");
                if (query.isBlank()) {
                    response = "Please provide a query after the command. Example: /rag my question";
                } else {
                    log.info("RAG command detected for chatId={}. Query: '{}'", chatId, query);
                    response = chatService.answerWithRag(chatId, query);
                }
            } else {
                // Delegate to AI layer
                response = chatService.handleUserMessage(chatId, userText);
            }

            log.info("Telegram response for chatId={}: {}", chatId, response);

            return response;
        }
        return null;
    }
}
