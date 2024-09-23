package com.example.notificationservice.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.notificationservice.model.Notification;
import com.example.notificationservice.repository.NotificationRepository;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    //@Autowired
    //private JavaMailSender javaMailSender;
    

    public void createNotification(Long eventId, Long userId, String email, String message, LocalDateTime notificationDate) {

        // Check if a notification already exists for this event and user
        if (notificationRepository.findByEventIdAndUserId(eventId, userId).isPresent()) {
            System.out.println("Notification already exists for eventId: " + eventId + " and userId: " + userId);
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

    // get the notifications by user id (user profile id): frontend should be able to send this
    public List<Notification> getPendingNotifications(Long userId) {
        return notificationRepository.findByUserIdAndNotifiedFalse(userId);
    }

    @Transactional
    public void deleteNotificationByEventId(Long eventId) {
        notificationRepository.deleteByEventId(eventId);
    }

    @Scheduled(cron = "0 0/2 * * * *") //scheduled to run every 2 minutes (for now), since there's no actual email configuration..
    public void sendNotifications() {
        LocalDateTime now = LocalDateTime.now();
        List<Notification> notifications = notificationRepository.findAll();

        for (Notification notification : notifications) {

            LocalDateTime eventDate = notification.getNotificationDate();
            LocalDateTime startSending = eventDate.minusDays(2); //start sending notifications 2 days before the event
            LocalDateTime stopSending = eventDate; //stop sending notifications at the event start time

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
        /* sending email logic
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo("user-email@example.com");  // Retrieve email based on userId or pass email somehoww???
        mailMessage.setSubject("Event Notification");
        mailMessage.setText(message);
        javaMailSender.send(mailMessage);
        */

        //for now print log
        System.out.println("Sending email to user " + email + "with message: " + message);
    }
}


