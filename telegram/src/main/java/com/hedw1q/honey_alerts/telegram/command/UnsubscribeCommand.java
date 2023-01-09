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
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class UnsubscribeCommand extends AbstractBotCommand {
    @Autowired
    private ChatHistoryInfoService chatHistoryInfoService;
    @Autowired
    private StreamService streamService;

    public UnsubscribeCommand() {
        super("unsubscribe", "Отписаться от уведомлений");
    }

    /**
     * @param parameters - Parameter 1: String platform name
     *                Parameter 2: String channelName
     */
    @Override
    public void executeCommand(Chat chat, String[] parameters) {
        String nickNameParameter = parameters.length > 0 ? parameters[0] : null;
        String platformParameter = parameters.length > 1 ? parameters[1] : null;

        if (platformParameter == null || platformParameter.isBlank() || nickNameParameter == null || nickNameParameter.isBlank()) { //no parameters= choose streamer to unsubscribe
            chooseStreamer(chat);
        } else {
            executeUnsubscribe(chat,nickNameParameter, platformParameter);
        }
    }

    private void chooseStreamer(Chat chat) {
        cancelPreviousCommand(chat.getId(),chatInfo.getMessageId(), chatInfo.getChatState().getIdentifier());

        List<SubscriptionDTO> subs = streamService.getUserSubscriptions(chat.getId());
        if (subs.isEmpty()) {
            tgSender.sendTextMessageToChannel(chat.getId(), "Список подписок пуст");
            return;
        }

        Message response = tgSender.sendTextMessageToChannel(chat.getId(),
                "Выберите стримера, от которого хотите отписаться", InlineKeyboardFactory.getUnsubscribeKeyboard(subs));

        chatHistoryInfoService.updateState(ChatHistoryInfo.ChatState.DELETE,response.getMessageId(),chat.getId());
    }

    private void executeUnsubscribe(Chat chat,String nickNameParameter,String platformParameter ){
        String returnMsg = "Ошибка";
        try {
            StreamPlatform platformEnum = StreamPlatform.parsePlatform(platformParameter);
            UserDTO userDTO = chatInfo.getUser().mapToDTO();
            returnMsg = streamService.removeSubscription(
                    new SubscriptionDTO(userDTO, new ChannelDTO(nickNameParameter, platformEnum)));
        } catch (IllegalArgumentException ex) {
            returnMsg = "Ошибка: неизвестная платформа";
        } finally {
            tgSender.sendEditMessage(chat.getId(), chatInfo.getMessageId(), returnMsg, InlineKeyboardFactory.getEmptyKeyboard());
            chatHistoryInfoService.resetChatState(chat.getId());
        }
    }

}
