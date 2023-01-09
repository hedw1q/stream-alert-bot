package com.hedw1q.honey_alerts.telegram;

import com.hedw1q.honey_alerts.telegram.model.ChatHistoryInfo;
import com.hedw1q.honey_alerts.telegram.model.User;
import com.hedw1q.honey_alerts.telegram.repository.ChatHistoryInfoRepository;
import com.hedw1q.honey_alerts.telegram.service.UserService;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TelegramApplicationTests {
    @Autowired
    UserService userService;
    @Autowired
    ChatHistoryInfoRepository repository;

    @Test
    void name() {
        try {
            userService.saveUser(new User(0L, "name", new ChatHistoryInfo(1L)));
            System.out.println(userService.findByTelegramId(0L));
            System.out.println(repository.findAll().get(0));

            userService.saveUser(new User(0L, "name", new ChatHistoryInfo(1L)));
        } catch (EntityExistsException ex) {
            System.out.println("exist");
        }
        // repository.updateState(ChatHistoryInfo.ChatState.ADD,1L);

        System.out.println(repository.findAll().get(0).getUser());
        System.out.println(userService.findByTelegramId(0L).getChatHistory());

    }
}
