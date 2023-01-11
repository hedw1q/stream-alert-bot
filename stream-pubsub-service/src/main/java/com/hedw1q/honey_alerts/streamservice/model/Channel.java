package com.hedw1q.honey_alerts.streamservice.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.hedw1q.honey_alerts.share.model.ChannelDTO;
import com.hedw1q.honey_alerts.share.model.StreamPlatform;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "channels")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@NamedEntityGraph(name = "Channel.subscriptions", attributeNodes = @NamedAttributeNode("subscriptions"))
public class Channel {
    @EmbeddedId
    @JsonUnwrapped
    private ChannelId id;

    @JsonAlias({"user_name", "login"})
    @Column(name = "channel_name", nullable = false)
    @NotNull(message = "Channel name should be not empty")
    private String name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "channel", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Subscription> subscriptions;


    public Channel(ChannelId id, String name) {
        this.id = id;
        this.name = name;
    }

    public ChannelDTO mapToDTO() {
        return new ChannelDTO(name, id.getPlatform());
    }

    public static Channel fromDTO(ChannelDTO dto) {
        return new Channel(new ChannelId(dto.id(),dto.platform()), dto.name());
    }

    public String getLink() {
        return id.getPlatform().getLink() + name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Channel channel = (Channel) o;

        return id != null ? id.equals(channel.id) : channel.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

