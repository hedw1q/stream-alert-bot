package com.hedw1q.honey_alerts.streamservice.service.pubsub.impl;

import com.hedw1q.honey_alerts.streamservice.model.Channel;
import com.hedw1q.honey_alerts.streamservice.model.ChannelId;
import com.hedw1q.honey_alerts.streamservice.model.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.Map;


public class GoodGameSubTest {
    private GoodGameSub goodGameSub;
    private Channel channel;

    @BeforeEach
    void setUp() {
        goodGameSub = new GoodGameSub();
        goodGameSub.setWebClient(WebClient.create());
        channel = new Channel(new ChannelId(null, goodGameSub.getPlatform()), "hedw1q");
    }

    @Test
    void checkChannelExist_exist() {
        String id = goodGameSub.checkChannelExist(channel);

        Assertions.assertNotNull(id);
    }

    @Test
    void checkChannelExist_Notexist() {
        String id = goodGameSub.checkChannelExist(new Channel(new ChannelId(null, goodGameSub.getPlatform()), ""));

        Assertions.assertNull(id);
    }

    @Test
    void getLiveStreamsRequest_empty() {
        Map<String, Stream> streams = goodGameSub.getLiveStreamsRequest(Collections.singletonList(channel));

        Assertions.assertEquals(0, streams.size());
    }
}