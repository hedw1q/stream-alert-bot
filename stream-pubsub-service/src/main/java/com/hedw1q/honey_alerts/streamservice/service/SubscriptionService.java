package com.hedw1q.honey_alerts.streamservice.service;

import com.hedw1q.honey_alerts.share.model.StreamPlatform;
import com.hedw1q.honey_alerts.share.model.SubscriptionDTO;
import com.hedw1q.honey_alerts.streamservice.model.Channel;
import com.hedw1q.honey_alerts.streamservice.model.Subscription;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.NotSupportedException;

import java.util.List;

public interface SubscriptionService {
    /**
     * Delete user's subscription
     * @param existingSub existing subscription DTO
     * @throws EntityNotFoundException if existingSub not found
     */
    void deleteSubscription(Subscription existingSub) throws EntityNotFoundException;

    /**
     * Create subscription
     * @param newSub new subscription DTO
     * @throws EntityExistsException if subscription already exists
     * @return created subscription
     */
    Subscription createSubscription(Subscription newSub) throws EntityExistsException, NotSupportedException;

    /**
     * Get list of subscriptions by user telegram id
     * @param userTgId telegram id
     * @return list of subcriptions
     */
    List<Subscription> getSubscriptionsByUserTelegramId(Long userTgId);

    /**
     * Find subscriptions by platform
     * @param platform Streaming platform
     * @return list of subcriptions
     */
    List<Subscription> findSubscriptionsByPlatform(StreamPlatform platform);

    /**
     * Get list of user's telegram Ids that subscribe channel
     * @param channel Channel to subscribe
     * @return list of Long telegram ids
     */
    List<Long> getUsersSubscribedByChannel(Channel channel);
}
