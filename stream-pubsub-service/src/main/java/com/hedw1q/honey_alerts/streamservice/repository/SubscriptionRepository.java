package com.hedw1q.honey_alerts.streamservice.repository;

import com.hedw1q.honey_alerts.share.model.StreamPlatform;
import com.hedw1q.honey_alerts.streamservice.model.Channel;
import com.hedw1q.honey_alerts.streamservice.model.Subscription;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {
    @EntityGraph(value = "Subscription.channel")
    List<Subscription> findAllByUserTgId(Long userTgId);

    @EntityGraph(value = "Subscription.channel")
    List<Subscription> findAllByChannelIdPlatform(StreamPlatform platform);

    @EntityGraph(value = "Subscription.channel")
    Optional<Subscription> findByChannelAndUserTgId(Channel channel, Long userTgId);

    @EntityGraph(value = "Subscription.channel")
    List<Subscription> findAllByChannel(Channel channel);
}

