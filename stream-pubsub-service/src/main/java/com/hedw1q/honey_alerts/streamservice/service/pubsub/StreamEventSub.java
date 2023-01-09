package com.hedw1q.honey_alerts.streamservice.service.pubsub;

import com.hedw1q.honey_alerts.streamservice.model.Channel;

public interface StreamEventSub {
    /**
     * Check every time piece if any event fired
     */
   void checkIfStreamEventFired();

    /**
     * Check if channel exist
     * @param channel
     * @return String channel id if channel exist; otherwise null
     */
   String checkChannelExist(Channel channel);
}
