package com.hedw1q.honey_alerts.telegram.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "chat_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder @ToString
public class ChatHistoryInfo {
    @Id
    @Column(name = "user_tg_id")
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_tg_id")
    private User user;

    @Column(name = "chat_id", nullable = false, unique = true)
    @NaturalId
    private Long chatId;

    @Column(name = "last_message_id")
    private Integer messageId;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "chat_state")
    private ChatState chatState = ChatState.READY;

    @Column(name = "parameters")
    private String parameters;

    public ChatHistoryInfo(Long chatId) {
        this.chatId = chatId;
    }

    public enum ChatState {
        READY("/undefined"),
        ADD("/subscribe"),
        DELETE("/unsubscribe"),
        SETTINGS("/settings");

        final String identifier;

        ChatState(String identifier) {
            this.identifier=identifier;
        }
        public String getIdentifier(){
            return identifier;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatHistoryInfo that = (ChatHistoryInfo) o;
        return Objects.equals(id, that.id) && Objects.equals(chatId, that.chatId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, chatId);
    }
}
