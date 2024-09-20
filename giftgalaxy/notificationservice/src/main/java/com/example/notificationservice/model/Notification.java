package com.example.notificationservice.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long eventId; //the event to which notification is associated
    private Long userId; //the user profile to which notification is associated 

    private String email; // email to send the notification to

    private String message;
    private LocalDateTime notificationDate;

    private boolean notified;

    
    
}
