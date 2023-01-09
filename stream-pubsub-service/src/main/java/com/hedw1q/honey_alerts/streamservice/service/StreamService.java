package com.hedw1q.honey_alerts.streamservice.service;

import com.hedw1q.honey_alerts.share.model.StreamPlatform;
import com.hedw1q.honey_alerts.streamservice.model.Channel;
import com.hedw1q.honey_alerts.streamservice.model.Stream;

import java.util.List;
import java.util.Optional;

public interface StreamService {
    /**
     * Saves new Stream entity
     * @param stream
     * @return saved stream
     */
    Stream saveStream(Stream stream);

    /**
     * Find stream entity by id and set StreamStatus field to OFFLINE value and set StreamFinishTime field to current timestamp
     * @param streamId Integer stream id
     */
    void setStreamOfflineById(String streamId);

    /**
     * Find stream entity by id and update viewerCount field
     * @param streamId Integer stream id
     */
    void updateViewerCount(int viewerCount, String streamId);

    /**
     * Find current stream with status=LIVE by channelName and Platform parameters
     * @param channel channel
     * @return found stream wrapped in Optional
     */
    Optional<Stream> findCurrentLiveStreamByChannel(Channel channel);

    /**
     * Find all streams with status=LIVE
     * @return list of live streams or empty list
     */
    List<Stream> getLiveStreams();

    /**
     * Find all streams with status=LIVE in certain platform
     * @param streamPlatform Streaming platform
     * @return list of live streams or empty list
     */
    List<Stream> getLiveStreamsByPlatform(StreamPlatform streamPlatform);

    /**
     * Check if channel live
     * @param channel
     * @return true if channel currently live
     */
    boolean isChannelOnline(Channel channel);
}
