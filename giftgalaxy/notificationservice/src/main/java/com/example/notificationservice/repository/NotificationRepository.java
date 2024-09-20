package com.example.notificationservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.notificationservice.model.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdAndNotifiedFalse(Long userId);
    Optional<Notification> findByEventIdAndUserId(Long eventId, Long userId);
    void deleteByEventId(Long eventId);
}
