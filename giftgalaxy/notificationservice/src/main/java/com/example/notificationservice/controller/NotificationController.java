package com.example.notificationservice.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.notificationservice.model.Notification;
import com.example.notificationservice.model.NotificationDto;
import com.example.notificationservice.repository.NotificationRepository;
import com.example.notificationservice.security.JwtService;
import com.example.notificationservice.service.NotificationService;

import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin(origins = {"http://localhost:8081", "http://localhost:3000"})
@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private JwtService jwtService;


    //render the notifications for userId that are about upcoming events (in the next two days)
    @GetMapping("/{userId}")
    public  ResponseEntity<List<Notification>> getUserNotifications(@PathVariable Long userId, HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            Long jwtUserId = jwtService.extractUserId(token);

            if (!userId.equals(jwtUserId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();  
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); 
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfToday = now.toLocalDate().atStartOfDay();  // Start of today
        LocalDateTime daysAhead = now.plusDays(2);  //events for the next two days
            
        List<Notification> notifications = notificationRepository.findByUserIdAndNotifiedFalse(userId).stream()
                .filter(notification -> notification.getNotificationDate().isAfter(startOfToday.minusSeconds(1)) 
                    && notification.getNotificationDate().isBefore(daysAhead))  // only future notifications up to 2 days ahead
                    .collect(Collectors.toList());

        return ResponseEntity.ok(notifications);
        }

    
    @PostMapping("/create")
    public ResponseEntity<String> createNotification(@RequestBody NotificationDto notificationDto) {
        System.out.println("In Notification Controller .. creating notification");
        notificationService.createNotification(
            notificationDto.getEventId(),
            notificationDto.getUserId(),
            notificationDto.getEmail(),
            notificationDto.getMessage(),
            notificationDto.getNotificationDate()
        );
    return ResponseEntity.ok("Notification created successfully");
    }


    

    // delete notification of event with eventId: called when that event is deleted or notify is set to false
    @DeleteMapping("/delete/{eventId}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long eventId) {
        notificationService.deleteNotificationByEventId(eventId);
        return ResponseEntity.noContent().build();
    }


    
}
