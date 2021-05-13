package com.lennart.telegram;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class MyAmazingBot extends TelegramLongPollingBot {

    @Override
    public String getBotToken() {
        return "1643465373:AAHhn9EfoGpccf48n3w5FaIRhwJ04yPDsPk";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();

            String botReturnMessageText = null;

            if(messageText.toLowerCase().contains("eije") || messageText.toLowerCase().contains("leon")) {
                if(Math.random() < 0.2) {
                    botReturnMessageText = "eije...";
                } else if(Math.random() < 0.4) {
                    botReturnMessageText = "hmm?";
                } else if(Math.random() < 0.6) {
                    botReturnMessageText = "watte?";
                } else if(Math.random() < 0.8) {
                    botReturnMessageText = "Tzeije?!";
                } else {
                    botReturnMessageText = "HMM?!?!";
                }
            } else {
                if(Math.random() < 0.01) {
                    double secondRandom = Math.random();

                    if(secondRandom < 0.1) {
                        botReturnMessageText = "UCHE UCHE!";
                    } else if(secondRandom < 0.2) {
                        botReturnMessageText = "HOU OP!";
                    } else if(secondRandom < 0.3) {
                        botReturnMessageText = "Ik meen het!";
                    } else if(secondRandom < 0.4) {
                        botReturnMessageText = "Nee... echt even ophouden nou";
                    } else if(secondRandom < 0.5) {
                        botReturnMessageText = "NEE IK MEEN HET!";
                    } else if(secondRandom < 0.6) {
                        botReturnMessageText = "Das ook zoiets...";
                    } else if(secondRandom < 0.7) {
                        botReturnMessageText = "Tuf tuf!";
                    } else if(secondRandom < 0.8) {
                        botReturnMessageText = "HatsjieeHatsjiee!";
                    } else if(secondRandom < 0.9) {
                        botReturnMessageText = "Oh boy.. weet iemand de Telegram username van Suuske?";
                    } else {
                        botReturnMessageText = "NEE OPHOUDEN!";
                    }
                }
            }

            if(botReturnMessageText != null) {
                SendMessage message = new SendMessage();
                message.setChatId(update.getMessage().getChatId().toString());
                message.setText(botReturnMessageText);

                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "eijebot";
    }
}
