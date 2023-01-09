package com.hedw1q.honey_alerts.streamservice.service.pubsub.impl.domain;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.hedw1q.honey_alerts.streamservice.model.Stream;
import com.hedw1q.honey_alerts.streamservice.model.StreamStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class GGStream {
    @JsonAlias({"id"})
    private String id;
    @JsonAlias("channel")
    private GGChannel channel;
    @JsonAlias("status")
    private StreamStatus streamStatus = StreamStatus.OFFLINE;
    @JsonAlias("broadcast_started")
    private Long streamStartTimeSeconds;
    @JsonAlias("viewers")
    private int viewerCount;

    Stream mapToStream() {
        return Stream.builder()
                .thumbnailUrlTemplate(channel.getThumbnailUrlTemplate())
                .game(channel.getGames().get(0).getTitle())
                .channel(channel.mapToChannel())
                .id(id)
                .title(channel.getTitle())
                .streamStatus(streamStatus)
                .streamStartTime(streamStartTimeSeconds == 0L ? Instant.now() : Instant.ofEpochSecond(streamStartTimeSeconds))
                .viewerCount(viewerCount)
                .build();
    }
}
