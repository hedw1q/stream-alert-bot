package com.hedw1q.honey_alerts.streamservice.service.pubsub.impl.domain;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.hedw1q.honey_alerts.share.model.StreamPlatform;
import com.hedw1q.honey_alerts.streamservice.model.Channel;
import com.hedw1q.honey_alerts.streamservice.model.ChannelId;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @NoArgsConstructor
public class GGChannel {

    @JsonAlias("id")
    private Integer id;
    @JsonAlias("key")
    private String name;

    @JsonAlias("title")
    private String title;

    @JsonAlias("games")
    private List<GGGame> games;

    @JsonAlias("thumb")
    private String thumbnailUrlTemplate;

    public String getThumbnailUrlTemplate() {
        return thumbnailUrlTemplate.startsWith("https:") ? thumbnailUrlTemplate : "https:" + thumbnailUrlTemplate;
    }

    @Data
    public static class GGGame {
        @JsonAlias("title")
        String title;
    }

    public Channel mapToChannel(){
        return new Channel(new ChannelId(id.toString(),StreamPlatform.GOODGAME), name);
    }
}
