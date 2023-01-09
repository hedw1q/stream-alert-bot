package com.hedw1q.honey_alerts.streamservice.service.pubsub.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedw1q.honey_alerts.share.model.StreamPlatform;
import com.hedw1q.honey_alerts.streamservice.model.Channel;
import com.hedw1q.honey_alerts.streamservice.model.ChannelId;
import com.hedw1q.honey_alerts.streamservice.model.Stream;
import com.hedw1q.honey_alerts.streamservice.model.StreamStatus;
import com.hedw1q.honey_alerts.streamservice.service.pubsub.AbstractStreamEventSub;
import com.hedw1q.honey_alerts.streamservice.service.pubsub.impl.domain.GGChannel;
import com.hedw1q.honey_alerts.streamservice.service.pubsub.impl.domain.GGStreamList;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component("gg")
@Slf4j
public class GoodGameSub extends AbstractStreamEventSub {
    private static final int REQUEST_THRESHOLD = 25;


    public GoodGameSub() {
        super(StreamPlatform.GOODGAME);
    }

    @Override
    public String checkChannelExist(Channel channel) {
        Optional<GGChannel> result = getWebClient().get()
                .uri("https://goodgame.ru/api/4/streams/" + channel.getName())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.empty())
                .bodyToMono(GGChannel.class)
                .blockOptional();
        if (result.isPresent() && result.get().getId() != null) {
            channel.setName(result.get().getName());
            return result.get().getId().toString();
        }
        return null;
    }

    @Override
    protected Map<String, Stream> getLiveStreamsRequest(List<Channel> channels) {
        List<List<Channel>> channelsSplitted = ListUtils.partition(channels, REQUEST_THRESHOLD);

        Map<String, Stream> liveChannelsFromRequest = new ConcurrentHashMap<>();

        for (List<Channel> channelList : channelsSplitted) {
            GGStreamList ggStreamList = getWebClient().get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("api2.goodgame.ru")
                            .path("v2/streams")
                            .queryParam("ids", channelList.stream().map(Channel::getId).map(ChannelId::getPlatformId).collect(Collectors.joining(",")))
                            .build())
                    .retrieve()
                    .bodyToMono(GGStreamList.class)
                    .doOnError(WebClientException.class, (WebClientException ex) -> log.error(ex.getMessage()))
                    .onErrorReturn(GGStreamList.empty())
                    .block();

            for (Stream stream : ggStreamList.mapToStreams().stream().filter(x->x.getStreamStatus().equals(StreamStatus.LIVE)).toList()) {
                stream.getChannel().getId().setPlatform(getPlatform());
                liveChannelsFromRequest.put(stream.getId(), stream);
            }
        }
        return liveChannelsFromRequest;
    }


}
