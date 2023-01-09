package com.hedw1q.honey_alerts.streamservice.service.impl;

import com.hedw1q.honey_alerts.share.model.StreamPlatform;
import com.hedw1q.honey_alerts.streamservice.model.Channel;
import com.hedw1q.honey_alerts.streamservice.model.ChannelId;
import com.hedw1q.honey_alerts.streamservice.model.Subscription;
import com.hedw1q.honey_alerts.streamservice.repository.ChannelRepository;
import com.hedw1q.honey_alerts.streamservice.repository.SubscriptionRepository;
import com.hedw1q.honey_alerts.streamservice.service.StreamService;
import com.hedw1q.honey_alerts.streamservice.service.pubsub.AbstractStreamEventSub;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceImplTest {
    @Mock
    private SubscriptionRepository repository;
    @Mock
    private ChannelRepository channelRepository;
    @Mock
    private ApplicationContext context;
    @Mock
    private AbstractStreamEventSub streamEventSub;
    @Mock
    private StreamService streamService;
    @InjectMocks
    private SubscriptionServiceImpl service;

    private Subscription sub;


    @BeforeEach
    void setUp() {
        sub = new Subscription(0L, new Channel(new ChannelId("123", StreamPlatform.TWITCH), "name"));
    }

    @Test
    void deleteSubscription_properly() {
        when(repository.findByChannelAndUserTgId(sub.getChannel(), sub.getUserTgId())).thenReturn(Optional.of(sub));
        when(channelRepository.findByNameAndIdPlatform(sub.getChannel().getName(), sub.getChannel().getId().getPlatform())).thenReturn(Optional.of(sub.getChannel()));


        Assertions.assertDoesNotThrow(() -> service.deleteSubscription(sub));
        verify(repository, atLeastOnce()).findByChannelAndUserTgId(any(), anyLong());
    }

    @Test
    void deleteSubscription_ChannelnotFound() {
        when(channelRepository.findByNameAndIdPlatform(sub.getChannel().getName(), sub.getChannel().getId().getPlatform())).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> service.deleteSubscription(sub));
    }

    @Test
    void deleteSubscription_subNotFound() {
        when(repository.findByChannelAndUserTgId(sub.getChannel(), sub.getUserTgId())).thenReturn(Optional.empty());
        when(channelRepository.findByNameAndIdPlatform(sub.getChannel().getName(), sub.getChannel().getId().getPlatform())).thenReturn(Optional.of(sub.getChannel()));

        Assertions.assertThrows(EntityNotFoundException.class, () -> service.deleteSubscription(sub));
    }

    @Test
    void createSubscription_properly() {
        when(repository.findByChannelAndUserTgId(sub.getChannel(), sub.getUserTgId())).thenReturn(Optional.empty());
        when(channelRepository.findByNameAndIdPlatform(sub.getChannel().getName(), sub.getChannel().getId().getPlatform())).thenReturn(Optional.of(sub.getChannel()));
        when(streamEventSub.checkChannelExist(any())).thenReturn("channel_id");
        when(repository.save(sub)).thenReturn(sub);
        when(context.getBean(anyString())).thenReturn(streamEventSub);


        Assertions.assertDoesNotThrow(() -> service.createSubscription(sub));
        verify(repository, atLeastOnce()).findByChannelAndUserTgId(any(), anyLong());
        verify(streamEventSub, atLeastOnce()).checkChannelExist(any());
    }

    @Test
    void createSubscription_AlreadyExist() {
        when(repository.findByChannelAndUserTgId(sub.getChannel(), sub.getUserTgId())).thenReturn(Optional.of(sub));
        when(channelRepository.findByNameAndIdPlatform(sub.getChannel().getName(), sub.getChannel().getId().getPlatform())).thenReturn(Optional.of(sub.getChannel()));

        Assertions.assertThrows(EntityExistsException.class, () -> service.createSubscription(sub));
        verify(repository, atLeastOnce()).findByChannelAndUserTgId(any(), anyLong());
    }

    @Test
    void createSubscription_ChannelNotExist() {
        when(repository.findByChannelAndUserTgId(sub.getChannel(), sub.getUserTgId())).thenReturn(Optional.empty());
        when(channelRepository.findByNameAndIdPlatform(sub.getChannel().getName(), sub.getChannel().getId().getPlatform())).thenReturn(Optional.of(sub.getChannel()));
        when(streamEventSub.checkChannelExist(any())).thenReturn(null);
        when(context.getBean(anyString())).thenReturn(streamEventSub);

        Assertions.assertThrows(EntityNotFoundException.class, () -> service.createSubscription(sub));
        verify(repository, atLeastOnce()).findByChannelAndUserTgId(any(), anyLong());
        verify(streamEventSub, atLeastOnce()).checkChannelExist(any());

    }

    @Test
    void getSubscriptionsByUserTelegramId_properly() {
        Subscription sub2 = new Subscription(0L, new Channel(new ChannelId("1234", StreamPlatform.TWITCH), "name"));

        List<Subscription> expected = Arrays.asList(sub, sub2);

        when(repository.findAllByUserTgId(0L)).thenReturn(expected);

        Assertions.assertEquals(expected, service.getSubscriptionsByUserTelegramId(0L));
        verify(repository, atLeastOnce()).findAllByUserTgId(anyLong());
    }
}