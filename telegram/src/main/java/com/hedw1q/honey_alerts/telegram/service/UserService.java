package com.hedw1q.honey_alerts.telegram.service;

import com.hedw1q.honey_alerts.telegram.model.User;
import jakarta.persistence.EntityExistsException;

public interface UserService {
    /**
     * Saves user to storage
     * @param user to save
     * @throws EntityExistsException if user already exist
     */
    User saveUser(User user) throws EntityExistsException;

    /**
     * Find user in storage by his Telegram id OR create new
     * @param tgId - unique Telegram user id
     * @return user
     */
    User findByTelegramId(Long tgId);

    void invertDisablePreview(Long tgId);

    void invertGameChangeNotifications(Long tgId);
}
