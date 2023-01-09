package com.hedw1q.honey_alerts.streamservice.model;

import com.hedw1q.honey_alerts.share.model.SubscriptionDTO;
import com.hedw1q.honey_alerts.share.model.UserDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "subscriptions")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "user_tg_id", nullable = false)
    private Long userTgId;
    //@MapsId("channelId")
    @JoinColumns({
            @JoinColumn(name="platform_id", referencedColumnName="platform_id"),
            @JoinColumn(name="platform", referencedColumnName="platform")
    })
   // @JoinColumn(name = "channel", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private Channel channel;

    public Subscription(Long userTgId, Channel channel) {
        this.userTgId = userTgId;
        this.channel = channel;
    }

    public SubscriptionDTO mapToDTO() {
        return new SubscriptionDTO(new UserDTO(getUserTgId(), null), channel.mapToDTO());
    }

    public static Subscription fromDTO(SubscriptionDTO dto) {
        return new Subscription(null, dto.user().tgId(), Channel.fromDTO(dto.channel()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Subscription that = (Subscription) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (userTgId != null ? !userTgId.equals(that.userTgId) : that.userTgId != null) return false;
        return channel != null ? channel.equals(that.channel) : that.channel == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (userTgId != null ? userTgId.hashCode() : 0);
        result = 31 * result + (channel != null ? channel.hashCode() : 0);
        return result;
    }
}
