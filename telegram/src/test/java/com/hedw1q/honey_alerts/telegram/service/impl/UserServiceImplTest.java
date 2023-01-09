package com.hedw1q.honey_alerts.telegram.service.impl;

import com.hedw1q.honey_alerts.telegram.model.User;
import com.hedw1q.honey_alerts.telegram.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    UserRepository userRepository;
    @InjectMocks
    UserServiceImpl userService;
    User user;

    @BeforeEach
    void setUp() {
        user = new User(0L, "tgName", true, false, null);
        when(userService.findByTelegramId(0L)).thenReturn(user);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void test_invertDisablePreview_properly() {
        doAnswer(invocationOnMock -> {
            user.setPreviewEnabled(!user.isPreviewEnabled());
            return null;
        }).when(userRepository).updatePreviewEnabled(0L,!user.isPreviewEnabled());

        userService.invertDisablePreview(0L);
        assertFalse(user.isPreviewEnabled());
        Mockito.verify(userRepository, atLeastOnce()).updatePreviewEnabled(any(),anyBoolean());
    }

    @Test
    void test_invertGameChangeNotifications_properly() {
        doAnswer(invocationOnMock -> {
            user.setGameChangeNotificationsEnabled(!user.isGameChangeNotificationsEnabled());
            return null;
        }).when(userRepository).updateGameChangeNotificationsEnabled(0L,!user.isGameChangeNotificationsEnabled());

        userService.invertGameChangeNotifications(0L);
        assertTrue(user.isPreviewEnabled());
        Mockito.verify(userRepository, atLeastOnce()).updateGameChangeNotificationsEnabled(any(),anyBoolean());
    }
}