package com.hedw1q.honey_alerts.streamservice.service.impl;

import com.hedw1q.honey_alerts.share.model.StreamPlatform;
import com.hedw1q.honey_alerts.streamservice.model.Channel;
import com.hedw1q.honey_alerts.streamservice.model.Stream;
import com.hedw1q.honey_alerts.streamservice.model.Subscription;
import com.hedw1q.honey_alerts.streamservice.repository.ChannelRepository;
import com.hedw1q.honey_alerts.streamservice.repository.SubscriptionRepository;
import com.hedw1q.honey_alerts.streamservice.service.StreamService;
import com.hedw1q.honey_alerts.streamservice.service.SubscriptionService;
import com.hedw1q.honey_alerts.streamservice.service.pubsub.AbstractStreamEventSub;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.NotSupportedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {
    @Autowired
    private SubscriptionRepository repository;
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private StreamService streamService;
    @Autowired
    private ApplicationContext context;

    @Override
    public void deleteSubscription(Subscription existingSub) throws EntityNotFoundException {
        Optional<Channel> channel = channelRepository.findByNameAndIdPlatform(existingSub.getChannel().getName(), existingSub.getChannel().getId().getPlatform());

        Optional<Subscription> sub =
                repository.findByChannelAndUserTgId(channel.orElseThrow(() -> new EntityNotFoundException("Channel not found")),
                        existingSub.getUserTgId());

        repository.delete(sub.orElseThrow(() -> new EntityNotFoundException("Subscription not found")));
    }

    @Override
    @Transactional
    public Subscription createSubscription(Subscription newSub) throws EntityExistsException, EntityNotFoundException, NotSupportedException {
        Optional<Channel> channel = channelRepository.findByNameAndIdPlatform(newSub.getChannel().getName(), newSub.getChannel().getId().getPlatform());
        if (channel.isPresent()
                && repository.findByChannelAndUserTgId(channel.get(), newSub.getUserTgId()).isPresent())
            throw new EntityExistsException("Already subscribed");

        AbstractStreamEventSub eventSub = switch (newSub.getChannel().getId().getPlatform()) {
            case TWITCH -> (AbstractStreamEventSub) context.getBean("twitch"); //WTF can not get from class
            case GOODGAME -> (AbstractStreamEventSub) context.getBean("gg");
            default -> throw new NotSupportedException("Streaming platform not supported");
        };

        String channelId = eventSub.checkChannelExist(newSub.getChannel());
        if (channelId == null) throw new EntityNotFoundException("Channel not found");

        newSub.getChannel().getId().setPlatformId(channelId);
        if (channel.isEmpty()) channelRepository.save(newSub.getChannel());
        Subscription savedSub = repository.save(newSub);

        Optional<Stream> channelStream = streamService.findCurrentLiveStreamByChannel(savedSub.getChannel());
        channelStream.ifPresent(eventSub::sendLiveNotification);
        eventSub.checkIfStreamEventFired();

        return savedSub;
    }


    @Override
    public List<Subscription> findSubscriptionsByPlatform(StreamPlatform platform) {
        return repository.findAllByChannelIdPlatform(platform);
    }

    @Override
    public List<Subscription> getSubscriptionsByUserTelegramId(Long userTgId) {
        return repository.findAllByUserTgId(userTgId);
    }

    @Override
    public List<Long> getUsersSubscribedByChannel(Channel channel) {
        return repository.findAllByChannel(channel)
                .stream()
                .map(Subscription::getUserTgId)
                .distinct()
                .toList();
    }
}
