package com.hedw1q.honey_alerts.streamservice.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "streams")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@NamedEntityGraph(name = "Stream.channel", attributeNodes = @NamedAttributeNode("channel"))
public class Stream {
    @Id
    @JsonAlias({"id"})
    private String id;

    @ManyToOne(fetch = FetchType.EAGER)
//    @MapsId("channelId")
    @JoinColumns({
            @JoinColumn(name = "platform_id", referencedColumnName = "platform_id"),
            @JoinColumn(name = "platform", referencedColumnName = "platform")
    })
    @JsonUnwrapped
    private Channel channel;

    @Enumerated(EnumType.STRING)
    @Column(name = "stream_status")
    @JsonAlias({"type", "status"})
    private StreamStatus streamStatus = StreamStatus.OFFLINE;

    @Column(name = "stream_start_time")
    @JsonAlias("started_at")
    private Instant streamStartTime;

    @Column(name = "stream_finish_time")
    private Instant streamFinishTime;

    @Column(name = "last_viewer_count")
    @JsonAlias({"viewer_count", "viewers"})
    private int viewerCount;
    @Column(name = "game")
    @JsonAlias("game_name")
    private String game;
    @Column(name = "title")
    @JsonAlias("title")
    private String title;

    @Column(name = "thumbnail_url_template")
    @JsonAlias({"thumbnail_url", "thumb"})
    private String thumbnailUrlTemplate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Stream stream = (Stream) o;

        if (id != null ? !id.equals(stream.id) : stream.id != null) return false;
        return channel != null ? channel.equals(stream.channel) : stream.channel == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (channel != null ? channel.hashCode() : 0);
        return result;
    }
}
