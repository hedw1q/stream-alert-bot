package com.hedw1q.honey_alerts.streamservice.repository;

import com.hedw1q.honey_alerts.share.model.ChannelDTO;
import com.hedw1q.honey_alerts.share.model.StreamPlatform;
import com.hedw1q.honey_alerts.share.model.UserDTO;
import com.hedw1q.honey_alerts.streamservice.model.Channel;
import com.hedw1q.honey_alerts.streamservice.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {
    List<Subscription> findAllByUserTgId(Long userTgId);

    List<Subscription> findAllByChannelIdPlatform(StreamPlatform platform);

    Optional<Subscription> findByChannelAndUserTgId(Channel channel, Long userTgId);

    List<Subscription> findAllByChannel(Channel channel);
}

