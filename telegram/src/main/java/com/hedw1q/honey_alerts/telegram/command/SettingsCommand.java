package com.hedw1q.honey_alerts.telegram.command;

import com.hedw1q.honey_alerts.telegram.bot.InlineKeyboardFactory;
import com.hedw1q.honey_alerts.telegram.model.ChatHistoryInfo;
import com.hedw1q.honey_alerts.telegram.service.ChatHistoryInfoService;
import com.hedw1q.honey_alerts.telegram.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Optional;

@Component
@Slf4j
public class SettingsCommand extends AbstractBotCommand {
    @Autowired
    private UserService userService;

    public SettingsCommand() {
        super("settings", "Настройки пользователя");
    }

    /**
     * @param parameters Parameter 1 - setting parameter
     */
    @Override
    public void executeCommand(Chat chat, String[] parameters) {
        String settingParameter = parameters.length > 0 ? parameters[0] : null;
        if (settingParameter == null || settingParameter.isBlank()) {
            chooseSettings(chat);
        } else {
            changeSettings(chat, settingParameter);
        }
    }

    private void chooseSettings(Chat chat) {
        cancelPreviousCommand(chat.getId(),chatInfo.getMessageId(), chatInfo.getChatState().getIdentifier());

        Message response = tgSender.sendTextMessageToChannel(chat.getId(),
                "Смена настроек:", InlineKeyboardFactory.getSettingsKeyboard(chatInfo.getUser()));

        chatHistoryInfoService.updateState(ChatHistoryInfo.ChatState.SETTINGS,response.getMessageId(),chat.getId());
    }

    private void changeSettings(Chat chat, String settingParameter) {
        try {
            switch (settingParameter) {
                case "preview" -> userService.invertDisablePreview(chatInfo.getUser().getTgId());
                case "gamenotif" -> userService.invertGameChangeNotifications(chatInfo.getUser().getTgId());
            }
        } finally {
            //chatInfo.ifPresent(c -> chatHistoryInfoService.updateState(ChatHistoryInfo.ChatState.CLOSED, c.getId()));
            tgSender.sendEditKeyboardMessage(chat.getId(), chatInfo.getMessageId(), InlineKeyboardFactory.getSettingsKeyboard(userService.findByTelegramId(chatInfo.getUser().getTgId())));
        }

    }
}
