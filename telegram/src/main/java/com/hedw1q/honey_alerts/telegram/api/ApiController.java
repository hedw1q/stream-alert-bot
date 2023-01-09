package com.hedw1q.honey_alerts.telegram.api;

import com.hedw1q.honey_alerts.share.model.TelegramApiMessage;
import com.hedw1q.honey_alerts.telegram.bot.TgBot;
import com.hedw1q.honey_alerts.telegram.model.User;
import com.hedw1q.honey_alerts.telegram.service.UserService;
import jakarta.validation.Valid;
import jakarta.ws.rs.QueryParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class ApiController {
    @Autowired
    private TgBot tgBot;
    @Autowired
    private UserService userService;

    @PostMapping(value = "/send", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void sendMessages(@RequestBody @Valid TelegramApiMessage msg, @RequestParam(defaultValue = "false") boolean additional) {
        log.info("Message request: {}", msg);
        for (Long tgId : msg.tgIds().stream().distinct().toList()) {
            User user = userService.findByTelegramId(tgId);
            if (additional && !user.isGameChangeNotificationsEnabled()) return;

            if (msg.attachmentURL() != null)
                tgBot.sendAttachmentMessageToChannel(tgId, msg.attachmentURL(), msg.message());
            else {
                tgBot.sendTextMessageToChannel(tgId, msg.message(), !user.isPreviewEnabled());
            }
        }
    }
}
