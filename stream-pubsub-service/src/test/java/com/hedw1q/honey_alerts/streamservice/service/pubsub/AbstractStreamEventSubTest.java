package com.hedw1q.honey_alerts.streamservice.service.pubsub;

import com.hedw1q.honey_alerts.share.model.StreamPlatform;
import com.hedw1q.honey_alerts.streamservice.model.*;
import com.hedw1q.honey_alerts.streamservice.service.StreamService;
import com.hedw1q.honey_alerts.streamservice.service.SubscriptionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AbstractStreamEventSubTest {

    @Mock
    StreamService streamService;
    @Mock
    SubscriptionService subscriptionService;
    @InjectMocks
    AbstractStreamEventSub streamEventSub = Mockito.mock(AbstractStreamEventSub.class, withSettings().useConstructor(StreamPlatform.TWITCH).defaultAnswer(CALLS_REAL_METHODS));

    Stream testStream;

    List<Subscription> subscriptions;

    @BeforeEach
    void setUp() {
        testStream = new Stream("stream_id_1", new Channel(new ChannelId("channel_id_1", StreamPlatform.TWITCH), "name1"), StreamStatus.LIVE, Instant.MIN, null, 80, "game1", "title1", "thumbnail1");

        subscriptions = java.util.stream.Stream.of(testStream)
                .map(stream -> new Subscription(null, 0L, stream.getChannel()))
                .toList();

        when(streamService.getLiveStreamsByPlatform(any())).thenReturn(Arrays.asList( //cache
                testStream));

        when(subscriptionService.findSubscriptionsByPlatform(StreamPlatform.TWITCH))
                .thenReturn(subscriptions);
        streamEventSub.init();
    }

    @AfterEach
    void tearDown() {
    }


    @Test
    @DisplayName("Should send Offline notification with testStream1 argument")
    void testCheckIfStreamEventFired_channelOffline() {
        when(streamEventSub.getLiveStreamsRequest(anyList())).thenReturn(Collections.emptyMap()); //actual

        streamEventSub.checkIfStreamEventFired();

        verify(streamEventSub).sendOfflineNotification(testStream);
        verify(streamEventSub, times(0)).sendLiveNotification(testStream);
        verify(streamEventSub, times(0)).sendGameChangeNotification(testStream);

        verify(streamEventSub).getLiveStreamsRequest(any());
    }

    @Test
    @DisplayName("Should send Live notification with testStream2 argument")
    void testCheckIfStreamEventFired_channelOnline() {
        Stream newStream = new Stream("stream_id_3", new Channel(new ChannelId("channel_id_3", StreamPlatform.TWITCH), "name3"), StreamStatus.LIVE, Instant.MIN, null, 80, "game3", "title3", "thumbnail3");

        when(streamEventSub.getLiveStreamsRequest(anyList())).thenReturn(
                java.util.stream.Stream.of(testStream,newStream)
                        .collect(Collectors.toMap(Stream::getId, stream -> stream))); //actual

        streamEventSub.checkIfStreamEventFired();

        verify(streamEventSub).sendLiveNotification(newStream);
        verify(streamEventSub, times(0)).sendOfflineNotification(newStream);
        verify(streamEventSub, times(0)).sendGameChangeNotification(newStream);

        verify(streamEventSub).getLiveStreamsRequest(any());
    }

    @Test
    @DisplayName("Should send Change game notification")
    void testCheckIfStreamEventFired_channelChangeGame() {
        Stream requestedCopyStream=Stream.builder()
                .id(testStream.getId())
                .viewerCount(testStream.getViewerCount())
                .streamStartTime(testStream.getStreamStartTime())
                .streamFinishTime(testStream.getStreamFinishTime())
                .streamStatus(testStream.getStreamStatus())
                .channel(testStream.getChannel())
                .title(testStream.getTitle())
                .thumbnailUrlTemplate(testStream.getThumbnailUrlTemplate())
                .game("newGame")
                .build();

        when(streamEventSub.getLiveStreamsRequest(anyList())).thenReturn(
                java.util.stream.Stream.of(requestedCopyStream)
                .collect(Collectors.toMap(Stream::getId, stream -> stream))); //actual


        streamEventSub.checkIfStreamEventFired();

        verify(streamEventSub).sendGameChangeNotification(testStream);

        verify(streamEventSub, times(0)).sendOfflineNotification(testStream);
        verify(streamEventSub, times(0)).sendLiveNotification(testStream);

        verify(streamEventSub).getLiveStreamsRequest(any());
    }
}
