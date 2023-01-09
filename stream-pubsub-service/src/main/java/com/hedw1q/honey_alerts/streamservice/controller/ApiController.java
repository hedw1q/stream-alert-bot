package com.hedw1q.honey_alerts.streamservice.controller;

import com.hedw1q.honey_alerts.share.model.SubscriptionDTO;
import com.hedw1q.honey_alerts.streamservice.model.Channel;
import com.hedw1q.honey_alerts.streamservice.model.Stream;
import com.hedw1q.honey_alerts.streamservice.model.Subscription;
import com.hedw1q.honey_alerts.streamservice.service.StreamService;
import com.hedw1q.honey_alerts.streamservice.service.SubscriptionService;
import jakarta.transaction.NotSupportedException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class ApiController {
    @Autowired
    private SubscriptionService service;
    @Autowired
    private StreamService streamService;

    @GetMapping(value = "/sub/user/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<SubscriptionDTO> getUserSubscriptions(@PathVariable("id") @NotNull(message = "Telegram ID should be set") Long userTelegramId, @RequestParam(defaultValue = "false") boolean online) {
        if (online) {
            List<Channel> liveChannels = streamService.getLiveStreams().stream()
                    .map(Stream::getChannel)
                    .toList();
            return service.getSubscriptionsByUserTelegramId(userTelegramId)
                    .stream()
                    .filter(e -> liveChannels.contains(e.getChannel()))
                    .map(Subscription::mapToDTO)
                    .toList();
        } else {
            return service.getSubscriptionsByUserTelegramId(userTelegramId)
                    .stream()
                    .map(Subscription::mapToDTO)
                    .toList();
        }
    }

    @PostMapping(value = "/sub/new", produces = MediaType.TEXT_PLAIN_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public String newSub(@RequestBody @Valid SubscriptionDTO newSub) throws NotSupportedException {
        log.info("new Subscription request: {}", newSub);
        service.createSubscription(Subscription.fromDTO(newSub));
        return String.format("Подписка на оповещения о стримах %s успешно создана", newSub.channel().getHTMLLink());
    }

    @PostMapping(value = "/sub/delete", produces = MediaType.TEXT_PLAIN_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public String deleteSub(@RequestBody @Valid SubscriptionDTO existingSub) {
        log.info("delete Subscription request: {}", existingSub);
        service.deleteSubscription(Subscription.fromDTO(existingSub));
        return String.format("Подписка на оповещения о стримах %s успешно удалена", existingSub.channel().getHTMLLink());
    }
}
