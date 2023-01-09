package com.hedw1q.honey_alerts.telegram.service;

import com.hedw1q.honey_alerts.share.model.SubscriptionDTO;

import java.util.List;

public interface StreamService {
    /**
     * Get channel subscriptions by user unique Telegram id
     *
     * @param tgId User telegram id
     * @return List of sub DTO
     */
    List<SubscriptionDTO> getUserSubscriptions(Long tgId);

    /**
     * Creates new subscription
     *
     * @param sub sub DTO
     * @return String message about success or failure
     */
    String createNewSubscription(SubscriptionDTO sub);

    /**
     * Deletes subscription
     *
     * @param sub sub DTO
     * @return String message about success or failure
     */
    String removeSubscription(SubscriptionDTO sub);

    /**
     * Get channel subscriptions currently ONLINE by user unique Telegram id
     *
     * @param tgId User telegram id
     * @return List of sub DTO
     */
    List<SubscriptionDTO> getOnlineUserSubscriptions(Long tgId);
}
