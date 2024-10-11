package com.example.notificationservice.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.notificationservice.model.Notification;
import com.example.notificationservice.repository.NotificationRepository;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private JavaMailSender javaMailSender;
    
    public void createNotification(Long eventId, Long userId, String email, String message, LocalDateTime notificationDate) {

        // Check if a notification already exists for this event and user
        if (notificationRepository.findByEventId(eventId).isPresent()) {
            System.out.println("Notification already exists for eventId: " + eventId + " ...maybe update");
            updateNotification(eventId, userId, email, message, notificationDate);
        return;  // Don't create a new notification if one already exists
        }
        Notification notification = new Notification();
        notification.setEventId(eventId);
        notification.setUserId(userId);
        notification.setEmail(email);
        notification.setMessage(message);
        notification.setNotificationDate(notificationDate);
        notification.setNotified(false);
        notificationRepository.save(notification);
    }

    // Update existing notification if exists
    public void updateNotification(Long eventId, Long userId, String email, String message, LocalDateTime notificationDate) {
        System.out.println("Updating notification...");
        Notification notification = notificationRepository.findByEventId(eventId)
                .orElseThrow(() -> new RuntimeException("Notification not found for eventId: " + eventId));

        notification.setEmail(email);
        notification.setMessage(message);
        notification.setNotificationDate(notificationDate);
        notification.setNotified(false);
        notificationRepository.save(notification);
    }

    // get the notifications by user id (user profile id): frontend should be able to send this
    public List<Notification> getPendingNotifications(Long userId) {
        return notificationRepository.findByUserIdAndNotifiedFalse(userId);
    }

    @Transactional
    public void deleteNotificationByEventId(Long eventId) {
        boolean exists = notificationRepository.existsByEventId(eventId);
        if (exists) {
            notificationRepository.deleteByEventId(eventId);
        } else {
            System.out.println("There were already no notifications for " + eventId);
        }
    }

    @Scheduled(cron = "0 0/10 * * * *") //scheduled to run every 2 minutes (for testing - for production, maybe every 6 hours???)
    public void sendNotifications() {
        LocalDateTime now = LocalDateTime.now();
        List<Notification> notifications = notificationRepository.findAll();

        for (Notification notification : notifications) {

            LocalDateTime eventDate = notification.getNotificationDate();
            LocalDateTime startSending = eventDate.minusDays(2); //start sending notifications 2 days before the event
            LocalDateTime stopSending = eventDate.plusDays(1); //stop sending notifications one day after the event
            try {
                if (now.isAfter(startSending) && now.isBefore(stopSending) && !notification.isNotified()) {
                    System.out.println("Processing notification: " + notification);
                    sendEmail(notification.getEmail(), notification.getMessage());
                } 
                else if (now.isAfter(stopSending)) {
                    notification.setNotified(true);
                    notificationRepository.save(notification);
                }
                } catch (Exception e) {
                    System.err.println("Error processing notification: " + e.getMessage());
                }
            }
        }
    

    private void sendEmail(String email, String message) {
        // sending email logic
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);  // Retrieve email based on userId or pass email somehoww???
        mailMessage.setSubject("GiftGalaxy: Event Notification");
        mailMessage.setText(message);
        javaMailSender.send(mailMessage);

        //Verify
        System.out.println("Sent email to user " + email + "with message: " + message);
    }


    // Schedule to delete old notifications
    /*@Scheduled(cron = "0 0 0 * * *") // Run every day at midnight
    @Transactional
    public void deleteOldNotifications() {
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        List<Notification> oldNotifications = notificationRepository.findByNotificationDateBefore(yesterday); //or findByNotifiedTrueAndNotificationDateBefore ????

        if (!oldNotifications.isEmpty()) {
            System.out.println("Deleting old notifications...");
            notificationRepository.deleteAll(oldNotifications);
        } else {
            System.out.println("No old notifications to delete.");
        }
    }*/
}


