package com.hedw1q.honey_alerts.share.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;


public record SubscriptionDTO(
        @JsonProperty("id")
        Integer id,
        @JsonProperty("user")
        @Valid
        @NotNull(message = "User should be not empty")
        UserDTO user,
        @NotNull(message = "Channel should be not empty")
        @Valid
        @JsonProperty("channel")
        ChannelDTO channel) {
        public SubscriptionDTO(UserDTO user, ChannelDTO channel) {
                this(null, user, channel);
        }
}
