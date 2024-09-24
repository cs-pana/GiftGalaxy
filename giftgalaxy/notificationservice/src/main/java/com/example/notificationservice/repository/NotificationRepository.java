package com.example.notificationservice.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.notificationservice.model.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdAndNotifiedFalse(Long userId);
    Optional<Notification> findByEventId(Long eventId);
    void deleteByEventId(Long eventId);
    boolean existsByEventId(Long eventId);
    List<Notification> findByNotificationDateBefore(LocalDateTime yesterday);
}
