package com.hedw1q.honey_alerts.telegram.command;

import com.hedw1q.honey_alerts.telegram.bot.InlineKeyboardFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;

@Component
public class MenuCommand extends AbstractBotCommand {
    public MenuCommand() {
        super("menu", "Главное меню");
    }

    @Override
    public void executeCommand(Chat chat, String[] parameters) {
        tgSender.sendTextMessageToChannel(chat.getId(), "", InlineKeyboardFactory.getMainKeyboard());
    }
}
