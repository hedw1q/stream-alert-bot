package com.hedw1q.honey_alerts.telegram.bot;

import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import static com.hedw1q.honey_alerts.telegram.bot.TgBot.PARAMETERS_SEPARATOR;

public class TgCallbackEventStrategy implements TgEventStrategy{
    @Override
    public String getQuery(Update update) {
       return update.getCallbackQuery().getData();
    }

    @Override
    public User getUser(Update update) {
        return update.getCallbackQuery().getFrom();
    }

    @Override
    public Chat getChat(Update update) {
        return update.getCallbackQuery().getMessage().getChat();
    }

    @Override
    public Message getMessage(Update update) {
        return update.getCallbackQuery().getMessage();
    }
}
