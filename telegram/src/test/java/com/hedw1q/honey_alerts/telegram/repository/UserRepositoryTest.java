package com.hedw1q.honey_alerts.telegram.repository;

import com.hedw1q.honey_alerts.telegram.bot.TgBot;
import com.hedw1q.honey_alerts.telegram.model.ChatHistoryInfo;
import com.hedw1q.honey_alerts.telegram.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        User user = new User(0L, "tgName", true, false, null);
        userRepository.save(user);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void should_FindByTgIdInCache(){
        userRepository.findByTgId(0L);
        assertNotNull(cacheManager.getCache("users").get(0L,User.class));
    }

    @Test
    void should_updatePreviewEnabled() {
        assertTrue(userRepository.findByTgId(0L).get().isPreviewEnabled());

        userRepository.updatePreviewEnabled(0L, false);

        assertFalse(userRepository.findByTgId(0L).get().isPreviewEnabled());
    }

    @Test
    void should_updateGameChangeNotificationsEnabled() {
        assertFalse(userRepository.findByTgId(0L).get().isGameChangeNotificationsEnabled());

        userRepository.updateGameChangeNotificationsEnabled(0L, true);

        assertTrue(userRepository.findByTgId(0L).get().isPreviewEnabled());
    }
}