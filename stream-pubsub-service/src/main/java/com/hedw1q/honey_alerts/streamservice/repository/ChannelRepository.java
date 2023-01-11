package com.hedw1q.honey_alerts.streamservice.repository;

import com.hedw1q.honey_alerts.share.model.StreamPlatform;
import com.hedw1q.honey_alerts.streamservice.model.Channel;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChannelRepository extends JpaRepository<Channel,String> {

    @EntityGraph(value = "Channel.subscriptions")
    Optional<Channel> findByNameAndIdPlatform(String name, StreamPlatform platform);
}
