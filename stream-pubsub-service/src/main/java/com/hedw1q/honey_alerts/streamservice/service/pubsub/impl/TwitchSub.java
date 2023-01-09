package com.hedw1q.honey_alerts.streamservice.service.pubsub.impl;

import com.hedw1q.honey_alerts.share.model.StreamPlatform;
import com.hedw1q.honey_alerts.streamservice.config.TwitchAuthData;
import com.hedw1q.honey_alerts.streamservice.model.Channel;
import com.hedw1q.honey_alerts.streamservice.model.Stream;
import com.hedw1q.honey_alerts.streamservice.service.pubsub.AbstractStreamEventSub;
import com.hedw1q.honey_alerts.streamservice.service.pubsub.impl.domain.TwitchChannelList;
import com.hedw1q.honey_alerts.streamservice.service.pubsub.impl.domain.TwitchStreamList;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component("twitch")
@Slf4j
public class TwitchSub extends AbstractStreamEventSub {
    private static final int REQUEST_THRESHOLD = 100;

    private static final Map<String, String> getenv = System.getenv();
    @Getter
    private static final TwitchAuthData twitchAuthData;

    static {
        twitchAuthData = new TwitchAuthData(
                getenv.get("twitch.oAuthToken"),
                getenv.get("twitch.clientId"),
                getenv.get("twitch.clientSecret"));
    }

    public TwitchSub() {
        super(StreamPlatform.TWITCH);
    }

    @Override
    public String checkChannelExist(Channel channel){
        Optional<TwitchChannelList> result = getWebClient().get()
                .uri("https://api.twitch.tv/helix/users?login=" + channel.getName())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + twitchAuthData.oAuthToken())
                .header("Client-Id", twitchAuthData.clientId())
                .retrieve()
                .bodyToMono(TwitchChannelList.class)
                .onErrorResume(WebClientResponseException.class, notFound -> Mono.empty())
                .blockOptional();

        if (result.isPresent() && !result.get().getChannels().isEmpty()) {
            channel.setName(result.get().getChannels().get(0).getLogin());
            return result.get().getChannels().get(0).getId();
        }
        return null;
    }

    @Override
    protected Map<String, Stream> getLiveStreamsRequest(List<Channel> channels) {
        List<List<Channel>> channelsSplitted = ListUtils.partition(channels, REQUEST_THRESHOLD);

        Map<String, Stream> liveChannelsFromRequest = new ConcurrentHashMap<>();

        for (List<Channel> channelList : channelsSplitted) {
            TwitchStreamList twitchStreamList = getWebClient().get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("api.twitch.tv")
                            .path("helix/streams")
                            .queryParam("user_login", channelList.stream().map(Channel::getName).toArray())
                            .build())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + twitchAuthData.oAuthToken())
                    .header("Client-Id", twitchAuthData.clientId())
                    .retrieve()
                    .bodyToMono(TwitchStreamList.class)
                    .doOnError(WebClientException.class, (WebClientException ex) -> {
                        log.error(ex.getMessage());
                    })
                    .onErrorReturn(new TwitchStreamList(Collections.emptyList()))
                    .block();

            for (Stream stream : twitchStreamList.getChannels()) {
                stream.getChannel().getId().setPlatform(getPlatform());
                liveChannelsFromRequest.put(stream.getId(), stream);
            }
        }
        return liveChannelsFromRequest;
    }



}
