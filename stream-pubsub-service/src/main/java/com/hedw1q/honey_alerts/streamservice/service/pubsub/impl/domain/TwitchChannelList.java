package com.hedw1q.honey_alerts.streamservice.service.pubsub.impl.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @NoArgsConstructor
public class TwitchChannelList {
    @JsonProperty("data")
    private List<ChannelInfo> channels;

    @Data
    public static class ChannelInfo {
        @JsonProperty("id")
        private String id;
        @JsonProperty("display_name")
        private String login;
    }
}
