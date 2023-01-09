package com.hedw1q.honey_alerts.telegram.repository;

import com.hedw1q.honey_alerts.telegram.model.ChatHistoryInfo;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatHistoryInfoRepository extends JpaRepository<ChatHistoryInfo, Long> {
    @Query(value = "UPDATE ChatHistoryInfo b " +
            "SET b.chatState=:state, b.messageId=:messageId " +
            "WHERE b.chatId=:chatId")
    @Modifying
    @Transactional
    void updateStateAndMessageIdByChatId(@Param("state") ChatHistoryInfo.ChatState state,@Param("messageId") Integer messageId, @Param("chatId") Long chatId);

    Optional<ChatHistoryInfo> findByChatId(Long chatId);
}
