package com.hedw1q.honey_alerts.telegram.service;

import com.hedw1q.honey_alerts.telegram.model.ChatHistoryInfo;

import java.util.Optional;

public interface ChatHistoryInfoService {
    /**
     * Find ChatHistory DTO by user telegram id
     *
     * @param tgId - Long Telegram user id
     * @return ChatHistory DTO
     */
    Optional<ChatHistoryInfo> findByTgId(Long tgId);

    /**
     * Find ChatHistory DTO by telegram chat id
     *
     * @param chatId Long telegram chat id
     * @return ChatHistory DTO
     */
    Optional<ChatHistoryInfo> findByChatId(Long chatId);

    /**
     * Set chat state to ChatState. READY
     *
     * @param chatId - Long Telegram chat id
     */
    void resetChatState(Long chatId);

    /**
     * Update chat state by DTO id
     *
     * @param state     - new chat state
     * @param chatId    - Long Telegram chat id
     * @param messageId message Id optional
     */
    void updateState(ChatHistoryInfo.ChatState state, Integer messageId, Long chatId);


}
