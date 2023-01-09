package com.hedw1q.honey_alerts.telegram.model;

import com.hedw1q.honey_alerts.share.model.UserDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Table(name = "users")
@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor @AllArgsConstructor
public class User {
    @Id
    private Long tgId;

    @Column(name = "telegram_name")
    private String tgName;

    @Column(name = "preview_enabled", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean previewEnabled=false;

    @Column(name = "game_change_notif_enabled", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean gameChangeNotificationsEnabled=true;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @ToString.Exclude
    private ChatHistoryInfo chatHistory;

    public User(Long tgId) {
        this.tgId = tgId;
    }

    public User(Long tgId, String tgName) {
        this.tgId = tgId;
        this.tgName = tgName;
    }

    public User(Long tgId, String tgName, ChatHistoryInfo chatHistory) {
        this.tgId = tgId;
        this.tgName = tgName;
        this.chatHistory=chatHistory;
        if(chatHistory!=null) chatHistory.setUser(this);
    }

    public UserDTO mapToDTO(){
        return new UserDTO(tgId,tgName);
    }

    public static User fromDTO(UserDTO dto){
        return new User(dto.tgId(), dto.tgName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(tgId, user.tgId) && Objects.equals(tgName, user.tgName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tgId, tgName);
    }
}
