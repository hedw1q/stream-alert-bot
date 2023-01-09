package com.hedw1q.honey_alerts.streamservice.service.pubsub.impl.domain;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hedw1q.honey_alerts.streamservice.model.Stream;
import com.hedw1q.honey_alerts.streamservice.model.StreamStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GGStreamList {
    @JsonProperty("_embedded")
    private GGStreamContainer streamContainer;


    public static GGStreamList empty(){
        return new GGStreamList(new GGStreamContainer(Collections.emptyList()));
    }

    public List<Stream> mapToStreams() {
        return this.getStreamContainer().getGgstreams().stream().map(GGStream::mapToStream).toList();
    }

    @Data
    @NoArgsConstructor @AllArgsConstructor
    private static class GGStreamContainer {
        @JsonProperty("streams")
        private List<GGStream> ggstreams;
    }
}

