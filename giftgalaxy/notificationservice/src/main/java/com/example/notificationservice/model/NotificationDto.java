package com.example.notificationservice.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private Long eventId; //the event to which notification is associated
        private Long userId; //the user profile to which notification is associated 

        private String email; // email to send the notification to

        private String message;
        private LocalDateTime notificationDate;

    
}
