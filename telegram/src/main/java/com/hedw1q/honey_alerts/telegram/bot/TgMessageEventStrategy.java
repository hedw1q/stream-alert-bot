package com.hedw1q.honey_alerts.telegram.bot;

import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import static com.hedw1q.honey_alerts.telegram.bot.TgBot.PARAMETERS_SEPARATOR;


public class TgMessageEventStrategy implements TgEventStrategy {
    @Override
    public String getQuery(Update update) {
        return update.getMessage().getText()
                .replace(PARAMETERS_SEPARATOR, "");
    }

    @Override
    public User getUser(Update update) {
        return update.getMessage().getFrom();
    }

    @Override
    public Chat getChat(Update update) {
        return update.getMessage().getChat();
    }

    @Override
    public Message getMessage(Update update) {
        return update.getMessage();
    }

}
