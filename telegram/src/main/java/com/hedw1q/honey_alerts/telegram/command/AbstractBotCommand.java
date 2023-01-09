package com.hedw1q.honey_alerts.telegram.command;

import com.hedw1q.honey_alerts.telegram.bot.TgBot;
import com.hedw1q.honey_alerts.telegram.model.ChatHistoryInfo;
import com.hedw1q.honey_alerts.telegram.service.ChatHistoryInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Slf4j
public abstract class AbstractBotCommand extends BotCommand {
    public AbstractBotCommand(String commandIdentifier, String description) {
        super(commandIdentifier, description);
    }

    @Autowired
    protected ChatHistoryInfoService chatHistoryInfoService;
    @Autowired
    protected TgBot tgSender;
    protected ChatHistoryInfo chatInfo;

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        chatInfo = chatHistoryInfoService.findByChatId(message.getChatId()).orElseGet(() -> new ChatHistoryInfo(message.getChatId()));
        super.processMessage(absSender, message, arguments);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        executeCommand(chat, strings);
    }

    public abstract void executeCommand(Chat chat, String[] parameters);

    public void cancelPreviousCommand(Long chatId, Integer messageId, String commandName) {
        if (messageId != null) {
            chatHistoryInfoService.resetChatState(chatId);
            tgSender.sendEditMessage(chatId, messageId, "Команда " + commandName + " была отменена");
        }
    }
}
