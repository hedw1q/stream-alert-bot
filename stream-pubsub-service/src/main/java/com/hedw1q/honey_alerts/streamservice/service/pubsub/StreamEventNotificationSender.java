package com.hedw1q.honey_alerts.streamservice.service.pubsub;

import com.hedw1q.honey_alerts.streamservice.model.Stream;

public interface StreamEventNotificationSender {
    /**
     * Send notification on stream online event
     *
     * @param stream Stream entity
     */
    void sendLiveNotification(Stream stream);

    /**
     * Send notification on stream offline event
     *
     * @param stream Stream entity
     */
    void sendOfflineNotification(Stream stream);

    /**
     * Send notification on stream change game event
     *
     * @param stream Stream entity
     */
    void sendGameChangeNotification(Stream stream);
}
