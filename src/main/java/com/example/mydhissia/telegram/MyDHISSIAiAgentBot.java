package com.example.mydhissia.telegram;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class MyDHISSIAiAgentBot extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(MyDHISSIAiAgentBot.class);

    private final String botToken;
    private final String botUsername;
    private final TelegramUpdateHandler updateHandler;

    public MyDHISSIAiAgentBot(String botToken, String botUsername, TelegramUpdateHandler updateHandler) {
        this.botToken = botToken;
        this.botUsername = botUsername;
        this.updateHandler = updateHandler;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        String response = updateHandler.handleUpdate(update);
        if (response != null && update.hasMessage()) {
            sendMessage(update.getMessage().getChatId(), response);
        }
    }

    private void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);

        int maxRetries = 3;
        int retryDelayMs = 1000;

        for (int i = 0; i < maxRetries; i++) {
            try {
                execute(message);
                return; // Success
            } catch (TelegramApiException e) {
                logger.error("Error sending message to Telegram (Attempt {}/{}): {}", i + 1, maxRetries, e.getMessage());
                if (i < maxRetries - 1) {
                    try {
                        Thread.sleep(retryDelayMs * (i + 1));
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                } else {
                    logger.error("Failed to send message after {} attempts.", maxRetries, e);
                }
            }
        }
    }
}
