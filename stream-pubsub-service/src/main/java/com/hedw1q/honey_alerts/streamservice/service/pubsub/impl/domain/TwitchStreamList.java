package com.hedw1q.honey_alerts.streamservice.service.pubsub.impl.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.hedw1q.honey_alerts.streamservice.model.Stream;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data @NoArgsConstructor @AllArgsConstructor
public class TwitchStreamList {
    @JsonProperty("data")
    private List<Stream> channels;

    @JsonSetter(nulls = Nulls.AS_EMPTY)
    public void setChannels(List<Stream> channels) {
        this.channels = channels;
    }
}
