package com.hedw1q.honey_alerts.share.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.EmbeddedId;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;


public record ChannelDTO(
        @JsonProperty("id")
        String id,
        @NotNull(message = "Channel name should be not empty")
        @JsonProperty("login")
        String name,
        @NotNull(message = "Platform should be not empty")
        @JsonProperty("platform")
        StreamPlatform platform) {
    private static final String MARKDOWN_LINK_FORMAT = "[%s](%s)";
    private static final String HTML_LINK_FORMAT = "<a href=\"%s\">%s</a>";

    public ChannelDTO(String name, StreamPlatform platform) {
        this(null, name,platform);
    }

    public String getHTMLLink() {
        return String.format(HTML_LINK_FORMAT, platform.getLink() + name, name);
    }

    public String getMarkDownLink() {
        return String.format(MARKDOWN_LINK_FORMAT, name, platform.getLink() + name);
    }

}
