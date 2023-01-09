package com.hedw1q.honey_alerts.telegram.repository;

import com.hedw1q.honey_alerts.telegram.model.User;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    @Cacheable(value = "users", key = "#tgId")
    Optional<User> findByTgId(Long tgId);

    @Modifying
    @Transactional
    @Query("update User u set u.previewEnabled = :previewEnabled where u.tgId = :id")
    @CacheEvict(value = "users", key = "#id")
    void updatePreviewEnabled(@Param(value = "id") Long id, @Param(value = "previewEnabled") boolean previewEnabled);

    @Modifying
    @Transactional
    @Query("update User u set u.gameChangeNotificationsEnabled = :gameChangeNotificationsEnabled where u.tgId = :id")
    @CacheEvict(value = "users", key = "#id")
    void updateGameChangeNotificationsEnabled(@Param(value = "id") Long id, @Param(value = "gameChangeNotificationsEnabled") boolean gameChangeNotificationsEnabled);
}
