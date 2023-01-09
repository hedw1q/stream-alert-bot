package com.hedw1q.honey_alerts.telegram;

import com.hedw1q.honey_alerts.telegram.bot.TgBot;
import com.hedw1q.honey_alerts.telegram.command.AbstractBotCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@Slf4j
public class TgBotInitializer {

    @Autowired
    public TgBotInitializer(TgBot tgBot, List<? extends AbstractBotCommand> commands) {
        try {
            new TelegramBotsApi(DefaultBotSession.class).registerBot(tgBot);
            for (IBotCommand command:commands){
                tgBot.register(command);
            }
            log.info("Telegram bot registered");
        } catch (TelegramApiException ex) {
            log.error("Bootstrap telegram bot error", ex);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(TgBotInitializer.class, args);
    }
}
