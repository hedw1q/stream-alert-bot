package com.hedw1q.honey_alerts.telegram.command;


import com.hedw1q.honey_alerts.share.model.SubscriptionDTO;
import com.hedw1q.honey_alerts.telegram.service.StreamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.List;

@Component
@Slf4j
public class CheckOnlineCommand extends AbstractBotCommand {

    @Autowired
    private StreamService streamService;

    public CheckOnlineCommand() {
        super("online", "Показать, кто сейчас онлайн");
    }

    @Override
    public void executeCommand(Chat chat, String[] parameters) {
        List<SubscriptionDTO> subs = streamService.getOnlineUserSubscriptions(chatInfo.getUser().getTgId());

        String text;
        if (subs.isEmpty()) text = "Все стримеры оффлайн:(";
        else {
            StringBuilder message = new StringBuilder();
            for (SubscriptionDTO sub : subs) {
                message.append(sub.channel().getHTMLLink())
                        .append(" (").append(sub.channel().platform()).append(")").append("\n");
            }
            text = message.toString();
        }
        tgSender.sendTextMessageToChannel(chat.getId(), text, true);
    }
}
