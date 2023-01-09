package com.hedw1q.honey_alerts.streamservice.service.impl;

import com.hedw1q.honey_alerts.share.model.StreamPlatform;
import com.hedw1q.honey_alerts.streamservice.model.Stream;
import com.hedw1q.honey_alerts.streamservice.repository.StreamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.ZoneId;

@ExtendWith(MockitoExtension.class)
class StreamServiceImplTest {
    @Mock
    private StreamRepository repository;


    @InjectMocks
    private StreamServiceImpl service;

    private Stream stream;

    @BeforeEach
    void setUp() {

    }
    @Test
    void createNewStream() {
    }

    @Test
    void setStreamOfflineById() {
    }

    @Test
    void updateViewerCount() {
    }

    @Test
    void findCurrentLiveStreamByChannelNameAndPlatform() {
    }
}