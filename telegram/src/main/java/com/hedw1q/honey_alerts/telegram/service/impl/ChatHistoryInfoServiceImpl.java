package com.hedw1q.honey_alerts.telegram.service.impl;

import com.hedw1q.honey_alerts.telegram.model.ChatHistoryInfo;
import com.hedw1q.honey_alerts.telegram.repository.ChatHistoryInfoRepository;
import com.hedw1q.honey_alerts.telegram.service.ChatHistoryInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class ChatHistoryInfoServiceImpl implements ChatHistoryInfoService {
    @Autowired
    private ChatHistoryInfoRepository chatHistoryInfoRepository;


    @Override
    public Optional<ChatHistoryInfo> findByTgId(Long tgId) {
        return chatHistoryInfoRepository.findById(tgId);
    }

    @Override
    public Optional<ChatHistoryInfo> findByChatId(Long chatId) {
        return chatHistoryInfoRepository.findByChatId(chatId);
    }

    @Override
    public void resetChatState(Long chatId) {
        updateState(ChatHistoryInfo.ChatState.READY, null, chatId);
    }

    @Override
    public void updateState(ChatHistoryInfo.ChatState state, Integer messageId, Long chatId) {
        chatHistoryInfoRepository.updateStateAndMessageIdByChatId(state, messageId, chatId);
    }

//    private Optional<ChatHistoryInfo> findLastActiveByChatId(Long chatId) {
//        return em.createQuery("SELECT b from ChatHistoryInfo b WHERE b.lastBotState!='CLOSED' AND b.chatId=:chatId ORDER BY b.messageId DESC", ChatHistoryInfo.class)
//                .setMaxResults(1)
//                .setParameter("chatId", chatId)
//                .getResultList().stream().findFirst();
//    }
}
