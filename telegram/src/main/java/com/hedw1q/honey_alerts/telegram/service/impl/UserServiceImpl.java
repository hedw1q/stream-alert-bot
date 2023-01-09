package com.hedw1q.honey_alerts.telegram.service.impl;

import com.hedw1q.honey_alerts.telegram.model.User;
import com.hedw1q.honey_alerts.telegram.repository.UserRepository;
import com.hedw1q.honey_alerts.telegram.service.UserService;
import jakarta.persistence.EntityExistsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public User saveUser(User user) throws EntityExistsException {
        if (userRepository.findByTgId(user.getTgId()).isPresent()) {
            throw new EntityExistsException("User already exist");
        } else {
            log.info("Saving user {}", user);
            return userRepository.save(user);
        }
    }

    @Override
    public User findByTelegramId(Long tgId) {
        return userRepository.findByTgId(tgId).orElseGet(() -> userRepository.save(new User(tgId)));
    }

    @Override
    public void invertDisablePreview(Long tgId) {
        userRepository.updatePreviewEnabled(tgId, !findByTelegramId(tgId).isPreviewEnabled());
    }

    @Override
    public void invertGameChangeNotifications(Long tgId) {
        userRepository.updateGameChangeNotificationsEnabled(tgId, !findByTelegramId(tgId).isGameChangeNotificationsEnabled());
    }
}
