package com.hedw1q.honey_alerts.telegram.command;

import com.hedw1q.honey_alerts.telegram.bot.InlineKeyboardFactory;
import com.hedw1q.honey_alerts.telegram.model.ChatHistoryInfo;
import com.hedw1q.honey_alerts.telegram.model.User;
import com.hedw1q.honey_alerts.telegram.service.UserService;
import jakarta.persistence.EntityExistsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Component
@Slf4j
public class StartCommand extends AbstractBotCommand {
    @Autowired
    private UserService userService;

    public StartCommand() {
        super("start", "Старт");
    }

    @Override
    public void execute(AbsSender absSender, org.telegram.telegrambots.meta.api.objects.User user, Chat chat, String[] strings) {
        try {
            userService.saveUser(new User(user.getId(), user.getUserName(), new ChatHistoryInfo(chat.getId())));
        } catch (EntityExistsException ex) {
            log.info("User tgId={} already exist", user.getId());
        }
        super.execute(absSender, user, chat, strings);
    }

    @Override
    public void executeCommand(Chat chat, String[] parameters) {
        tgSender.sendTextMessageToChannel(chat.getId(), "Привет!", InlineKeyboardFactory.getMainKeyboard());
    }
}
