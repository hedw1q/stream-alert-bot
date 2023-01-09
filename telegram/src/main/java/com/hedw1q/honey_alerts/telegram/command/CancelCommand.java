package com.hedw1q.honey_alerts.telegram.command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Component
@Slf4j
public class CancelCommand extends AbstractBotCommand {

    public CancelCommand() {
        super("cancel", "Отменить команду");
    }

    /**
     * @param parameters Parameter 1 - String message ID to cancel
     */
    @Override
    public void executeCommand(Chat chat, String[] parameters) {
        cancelPreviousCommand(chat.getId(), chatInfo.getMessageId(), chatInfo.getChatState().getIdentifier());
    }
}
