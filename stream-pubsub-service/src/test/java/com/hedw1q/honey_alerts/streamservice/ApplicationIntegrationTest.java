package com.hedw1q.honey_alerts.streamservice;

import com.hedw1q.honey_alerts.share.model.StreamPlatform;
import com.hedw1q.honey_alerts.streamservice.model.Channel;
import com.hedw1q.honey_alerts.streamservice.model.ChannelId;
import com.hedw1q.honey_alerts.streamservice.model.Subscription;
import com.hedw1q.honey_alerts.streamservice.repository.ChannelRepository;
import com.hedw1q.honey_alerts.streamservice.repository.SubscriptionRepository;
import com.hedw1q.honey_alerts.streamservice.service.StreamService;
import com.hedw1q.honey_alerts.streamservice.service.SubscriptionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ApplicationIntegrationTest {

@Autowired
    ChannelRepository channelRepository;
@Autowired
    SubscriptionRepository subscriptionRepository;
@Autowired
SubscriptionService service;
    @BeforeEach
    void setUp() {

    }
    @Test
    void testCreateSubscription() {
    }

    @AfterEach
    void tearDown() {
        //subscriptionRepository.deleteAll();
    }
}
