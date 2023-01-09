package com.hedw1q.honey_alerts.streamservice.service.pubsub;

import com.hedw1q.honey_alerts.share.model.StreamPlatform;
import com.hedw1q.honey_alerts.share.model.TelegramApiMessage;
import com.hedw1q.honey_alerts.streamservice.model.Channel;
import com.hedw1q.honey_alerts.streamservice.model.Stream;
import com.hedw1q.honey_alerts.streamservice.model.Subscription;
import com.hedw1q.honey_alerts.streamservice.service.StreamService;
import com.hedw1q.honey_alerts.streamservice.service.SubscriptionService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;

@RequiredArgsConstructor
@Setter
@Getter
@Slf4j
public abstract class AbstractStreamEventSub implements StreamEventSub, StreamEventNotificationSender {
    private static final int CHECK_RATE_MINUTES = 5;
    private static final int SYNC_RATE_MINUTES = 30;

    private final ExecutorService checkerThreadPool = Executors.newFixedThreadPool(3);
    private final StreamPlatform platform;
    @Autowired
    @Getter
    private WebClient webClient;
    @Autowired
    private StreamService streamService;
    @Autowired
    private SubscriptionService subscriptionService;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    //KEY=STREAM ID, VALUE=STREAM
    private final Map<String, Stream> cachedLiveStreams = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        for (Stream stream : streamService.getLiveStreamsByPlatform(platform)) {
            cachedLiveStreams.put(stream.getId(), stream);
        }
    }

    @Override
    @Async
    @Scheduled(fixedRate = CHECK_RATE_MINUTES * 60 * 1000)
    public void checkIfStreamEventFired() {
        log.info("Checking cache: {}", platform);
        List<Channel> channels = subscriptionService.findSubscriptionsByPlatform(platform) // all channels by platform
                .stream()
                .map(Subscription::getChannel)
                .distinct()
                .toList();

        Map<String, Stream> requestedLiveStreams = getLiveStreamsRequest(channels); //requested data from platform API

        List<Callable<Void>> tasks = new ArrayList<>();

        Callable<Void> onlineTask = () -> {
            requestedLiveStreams.entrySet().parallelStream()
                    .filter(entry -> cachedLiveStreams.containsKey(entry.getKey()))
                    .filter(Predicate.not(entry -> cachedLiveStreams.get(entry.getKey()).getGame().equals(entry.getValue().getGame())))
                    .map(Map.Entry::getValue)
                    .forEach(this::onChannelChangeGame);
            return null;
        };
        tasks.add(onlineTask);

        Callable<Void> nowOfflineTask = () -> {
            cachedLiveStreams.entrySet().parallelStream()
                    .filter(Predicate.not(entry -> requestedLiveStreams.containsKey(entry.getKey())))
                    .map(Map.Entry::getValue)
                    .forEach(this::onChannelOffline);
            return null;
        };
        tasks.add(nowOfflineTask);

        Callable<Void> nowOnlineTask = () -> {
            requestedLiveStreams.entrySet().parallelStream()
                    .filter(Predicate.not(entry -> cachedLiveStreams.containsKey(entry.getKey())))
                    .map(Map.Entry::getValue)
                    .forEach(this::onChannelLive);
            return null;
        };
        tasks.add(nowOnlineTask);

        try {
            checkerThreadPool.invokeAll(tasks);
        } catch (InterruptedException interruptedException) {
            log.warn("Interrupted:", interruptedException);
        }

//        for (String cachedStreamId : cachedLiveStreams.keySet()) {
//            if (requestedLiveStreams.get(cachedStreamId) == null) { //stream in cache, but current offline
//                onChannelOffline(cachedLiveStreams.get(cachedStreamId));
//            } else if (!requestedLiveStreams.get(cachedStreamId).getGame().equals(cachedLiveStreams.get(cachedStreamId).getGame())) { //game changed
//                onChannelChangeGame(requestedLiveStreams.get(cachedStreamId));
//            } else { //stream online
//                cachedLiveStreams.get(cachedStreamId).setViewerCount(requestedLiveStreams.get(cachedStreamId).getViewerCount()); //update viewer count
//            }
//        }
//
//        for (String currentStreamId : requestedLiveStreams.keySet()) {
//            if (cachedLiveStreams.get(currentStreamId) == null) { //stream current online, but not stored in cache
//                onChannelLive(requestedLiveStreams.get(currentStreamId));
//            }
//        }
    }

    @Async
    @Scheduled(fixedRate = SYNC_RATE_MINUTES * 60 * 1000, initialDelay = SYNC_RATE_MINUTES * 1000)
    public void syncCacheWithDb() {
        log.info("Syncing cache: {}", platform);

        for (Stream cachedStream : cachedLiveStreams.values()) {
            streamService.saveStream(cachedStream);
        }
    }

    /**
     * Send request to streaming platform API and get live streams by channels
     *
     * @param channels List of channel for request
     * @return Map with String stream ID keys and Stream entity values
     */
    protected abstract Map<String, Stream> getLiveStreamsRequest(List<Channel> channels);

    @Override
    public void sendLiveNotification(Stream stream) {
        String messageTemplate = """
                ❗️%s онлайн на %s ❗️
                Название: %s
                Категория: %s
                                
                Ссылка: %s
                """;
        String thumbnailUrl = stream.getThumbnailUrlTemplate().replace("{width}", "1600").replace("{height}", "900");

        TelegramApiMessage msg = new TelegramApiMessage(subscriptionService.getUsersSubscribedByChannel(stream.getChannel()),
                String.format(messageTemplate,
                        stream.getChannel().getName(), stream.getChannel().getId().getPlatform(),
                        stream.getTitle(),
                        stream.getGame(),
                        stream.getChannel().getLink()),
                thumbnailUrl);

        sendMessage(msg, true);
    }

    @Override
    public void sendOfflineNotification(Stream stream) {
        String messageTemplate = """
                Стрим %s на %s закончен️
                Длительность: %s
                Зрителей: %s
                """;
        Duration streamDuration;
        try {
            streamDuration = Duration.between(stream.getStreamStartTime(), Instant.now());
        } catch (Exception e) {
            log.error("Exception on stream duration computing: ", e);
            streamDuration = Duration.ZERO;
        }
        String streamDurationFormatted = String.format("%d ч. %d мин.", streamDuration.toHours(), streamDuration.toMinutes() - streamDuration.toHours() * 60);

        TelegramApiMessage msg = new TelegramApiMessage(subscriptionService.getUsersSubscribedByChannel(stream.getChannel()),
                String.format(messageTemplate,
                        stream.getChannel().mapToDTO().getHTMLLink(), stream.getChannel().getId().getPlatform(),
                        streamDurationFormatted,
                        stream.getViewerCount()),
                null);

        sendMessage(msg, true);
    }

    @Override
    public void sendGameChangeNotification(Stream stream) {
        String messageTemplate = """
                %s сменил(а) игру на стриме️
                Название: %s
                Категория: %s
                                
                Ссылка: %s
                """;
        String thumbnailUrl = stream.getThumbnailUrlTemplate().replace("{width}", "1600").replace("{height}", "900");

        TelegramApiMessage msg = new TelegramApiMessage(subscriptionService.getUsersSubscribedByChannel(stream.getChannel()),
                String.format(messageTemplate,
                        stream.getChannel().getName(),
                        stream.getTitle(),
                        stream.getGame(),
                        stream.getChannel().getLink()),
                thumbnailUrl);

        sendMessage(msg, false);
    }


    private void onChannelLive(Stream stream) {
        log.info("Live: " + stream);

        sendLiveNotification(stream);

        streamService.saveStream(stream);
        cachedLiveStreams.put(stream.getId(), stream);
    }

    private void onChannelOffline(Stream stream) {
        log.info("Off: " + stream);

        sendOfflineNotification(stream);

        streamService.setStreamOfflineById(stream.getId());
        cachedLiveStreams.remove(stream.getId());
    }

    private void onChannelChangeGame(Stream stream) {
        log.info("Game change: {}", stream);

        sendGameChangeNotification(stream);

        cachedLiveStreams.get(stream.getId()).setGame(stream.getGame());
    }

    private void sendMessage(TelegramApiMessage message, boolean required) {
        webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("localhost")
                        .port(8081)
                        .path("api/v1/send")
                        .queryParam("additional", !required)
                        .build())
                .body(BodyInserters.fromValue(message))
                .retrieve()
                .toBodilessEntity()
                .doOnError(WebClientException.class, (WebClientException ex) -> log.error("Error send to telegram chat: ", ex))
                .block();
    }

}
