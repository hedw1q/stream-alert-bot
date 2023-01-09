package com.hedw1q.honey_alerts.telegram.command;

import com.hedw1q.honey_alerts.share.model.ChannelDTO;
import com.hedw1q.honey_alerts.share.model.StreamPlatform;
import com.hedw1q.honey_alerts.share.model.SubscriptionDTO;
import com.hedw1q.honey_alerts.share.model.UserDTO;
import com.hedw1q.honey_alerts.telegram.bot.InlineKeyboardFactory;
import com.hedw1q.honey_alerts.telegram.model.ChatHistoryInfo;
import com.hedw1q.honey_alerts.telegram.service.ChatHistoryInfoService;
import com.hedw1q.honey_alerts.telegram.service.StreamService;
import com.hedw1q.honey_alerts.telegram.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Collections;
import java.util.Optional;

@Component
@Slf4j
public class SubscribeCommand extends AbstractBotCommand {
    @Autowired
    private ChatHistoryInfoService chatHistoryInfoService;
    @Autowired
    private StreamService streamService;

    public SubscribeCommand() {
        super("subscribe", "Подписаться на уведомления");
    }

    @Override
    public void executeCommand(Chat chat, String[] parameters) {
        String nickNameParameter = parameters.length > 0 ? parameters[0] : null;
        String platformParameter = parameters.length > 1 ? parameters[1] : null;

        if ((nickNameParameter == null || nickNameParameter.isEmpty()) && (platformParameter == null || platformParameter.isBlank())) {
            chooseStreamer(chat);
        } else if ((nickNameParameter != null && !nickNameParameter.isBlank()) && (platformParameter == null || platformParameter.isBlank())) {
            choosePlatform(chat, nickNameParameter);
        } else if (nickNameParameter != null && !nickNameParameter.isBlank() && platformParameter != null && !platformParameter.isBlank()) {
            executeUnsubscribe(chat, nickNameParameter, platformParameter);
        }
    }

    private void chooseStreamer(Chat chat) {
        cancelPreviousCommand(chat.getId(),chatInfo.getMessageId(), chatInfo.getChatState().getIdentifier());

        Message response = tgSender.sendTextMessageToChannel(chat.getId(), "Введите ник стримера");

        chatHistoryInfoService.updateState(ChatHistoryInfo.ChatState.ADD,response.getMessageId(),chat.getId());
    }

    private void choosePlatform(Chat chat, String nickName) {
        String returnMsg = "Ошибка";
        try {
            tgSender.sendEditMessage(chat.getId(), chatInfo.getMessageId(), "Выберите платформу", InlineKeyboardFactory.getPlatformChooseKeyboard(this, nickName));
        } catch (Exception e) {
            tgSender.sendTextMessageToChannel(chat.getId(), "Ошибка");
            chatHistoryInfoService.resetChatState(chat.getId());
        }
    }

    private void executeUnsubscribe(Chat chat, String nickName, String platform) {
        String returnMsg = "Ошибка";
        try {
            StreamPlatform platformEnum = StreamPlatform.parsePlatform(platform);
            UserDTO userDTO = chatInfo.getUser().mapToDTO();
            returnMsg = streamService.createNewSubscription(
                    new SubscriptionDTO(userDTO, new ChannelDTO(nickName, platformEnum)));
        } catch (IllegalArgumentException ex) {
            returnMsg = "Ошибка: неизвестная платформа";
        } finally {
            tgSender.sendEditMessage(chat.getId(), chatInfo.getMessageId(), returnMsg, InlineKeyboardFactory.getEmptyKeyboard());
            chatHistoryInfoService.resetChatState(chat.getId());
        }
    }

}
