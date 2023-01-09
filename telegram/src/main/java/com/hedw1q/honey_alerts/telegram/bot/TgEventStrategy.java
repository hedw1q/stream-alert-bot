package com.hedw1q.honey_alerts.telegram.bot;

import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Arrays;

import static com.hedw1q.honey_alerts.telegram.bot.TgBot.PARAMETERS_SEPARATOR;

public interface TgEventStrategy {
    String getQuery(Update update);

    User getUser(Update update);

    Chat getChat(Update update);

    Message getMessage(Update update);

    /**
     * @param query like /unsubscribe_21
     * @return parameters array with removed /command
     */
    default String[] parseQuery(String query) {
        String[] splittedQuery = query.split(PARAMETERS_SEPARATOR);
        return (String[]) Arrays.stream(splittedQuery).filter(item -> !item.startsWith("/")).toArray(String[]::new);
    }
}
