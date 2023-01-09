package com.hedw1q.honey_alerts.streamservice.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.hedw1q.honey_alerts.share.model.StreamPlatform;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data @AllArgsConstructor @NoArgsConstructor
public class ChannelId implements Serializable {
    @Column(name = "platform_id")
    @JsonAlias("user_id")
    private String platformId;
    @Enumerated(EnumType.STRING)
    @Column(name = "platform")
    private StreamPlatform platform;
}
