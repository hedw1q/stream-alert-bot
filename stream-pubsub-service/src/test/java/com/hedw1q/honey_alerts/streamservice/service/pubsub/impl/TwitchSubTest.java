package com.hedw1q.honey_alerts.streamservice.service.pubsub.impl;

import com.hedw1q.honey_alerts.streamservice.model.Channel;
import com.hedw1q.honey_alerts.streamservice.model.ChannelId;
import com.hedw1q.honey_alerts.streamservice.model.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.web.reactive.function.client.WebClient;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

import java.util.Collections;
import java.util.Map;

@ExtendWith(SystemStubsExtension.class)
class TwitchSubTest {
    @SystemStub
    private static EnvironmentVariables environmentVariables;

    private TwitchSub twitchSub;
    private Channel channel;

    @BeforeAll
    static void beforeAll() {
        environmentVariables.set("twitch.clientId", "gp762nuuoqcoxypju8c569th9wz7q5") //just random account:)
                .set("twitch.oAuthToken", "g4blfcmmx177auarf9goq8f4a8j121");
    }

    @BeforeEach
    void setUp() {
        twitchSub = new TwitchSub();
        twitchSub.setWebClient(WebClient.create());
        channel = new Channel(new ChannelId(null, twitchSub.getPlatform()), "hedw1q");
    }

    @Test
    void checkChannelExist_exist() {
        String id = twitchSub.checkChannelExist(channel);

        Assertions.assertNotNull(id);
    }

    @Test
    void checkChannelExist_Notexist() {
        String id = twitchSub.checkChannelExist(new Channel(new ChannelId(null, twitchSub.getPlatform()), ""));

        Assertions.assertNull(id);
    }

    @Test
    void getLiveStreamsRequest_empty() {
        Map<String, Stream> streams=twitchSub.getLiveStreamsRequest(Collections.singletonList(channel));

        Assertions.assertEquals(0, streams.size());
    }
}