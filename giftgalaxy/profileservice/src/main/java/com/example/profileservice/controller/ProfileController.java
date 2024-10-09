package com.example.profileservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import com.example.profileservice.dto.EventDto;
import com.example.profileservice.dto.UserProfileDto;
import com.example.profileservice.model.Event;
import com.example.profileservice.model.UserProfile;
import com.example.profileservice.service.EventService;
import com.example.profileservice.service.UserProfileService;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
@RequestMapping("/profiles")
public class ProfileController {

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private EventService eventService;

    //for communication with notification service
    @Autowired
    private RestTemplate restTemplate;
    String notificationUrl = "http://notification-service:8082/notifications";
    

    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getUserProfile(HttpServletRequest request) {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserProfileDto userProfile = userProfileService.getUserProfileByEmail(email);
        return ResponseEntity.ok(userProfile);
    }   


    @PostMapping("/create")
    public ResponseEntity<UserProfile> createProfile(@RequestBody UserProfileDto userProfileDto) {
        UserProfile createdProfile = userProfileService.createUserProfile(userProfileDto);
        return new ResponseEntity<>(createdProfile, HttpStatus.CREATED);
    }

    //Event Management

    @GetMapping("/me/events")
    public ResponseEntity<List<EventDto>> getUserEvents(HttpServletRequest request) {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<EventDto> events = eventService.getEventsForUser(email);
        return ResponseEntity.ok(events);

    }

    @PostMapping("/me/events")
    public ResponseEntity<Event> createEvent(@RequestBody EventDto eventDto, HttpServletRequest request) {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Event newEvent = eventService.createEventForUser(email, eventDto);

        if (newEvent.isNotify()) {
            sendNotification(newEvent, request); //if notifications are enabled for the event, create notification
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(newEvent);
    }

    @PutMapping("/me/events/{eventId}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long eventId, @RequestBody EventDto eventDto, HttpServletRequest request) {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Event updatedEvent = eventService.updateEventForUser(email, eventId, eventDto);
        System.out.println("Updating event...");
        if(!updatedEvent.isNotify()) {
            deleteNotification(eventId, request); }
        else {
            sendNotification(updatedEvent, request);
        }

        return ResponseEntity.ok(updatedEvent);
    }

    @DeleteMapping("/me/events/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long eventId, HttpServletRequest request) {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        eventService.deleteEventForUser(email, eventId);
        deleteNotification(eventId, request);
        return ResponseEntity.noContent().build();
    }
/*
    @PutMapping("/me")
    public ResponseEntity<UserProfile> updateProfile(@RequestBody UserProfileDto userProfileDto) {
     String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
     UserProfile updatedProfile = userProfileService.updateUserProfileByEmail(email, userProfileDto);
     return ResponseEntity.ok(updatedProfile);
}
*/
    @PutMapping("/update-profile")
    public ResponseEntity<UserProfile> updateProfile(@RequestBody UserProfileDto userProfileDto) {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserProfile updatedProfile = userProfileService.updateUserProfileByEmail(email, userProfileDto);
        //nuova riga
        userProfileDto.setUserId(updatedProfile.getUserId());

        return ResponseEntity.ok(updatedProfile);
    }

    private void sendNotification(Event event, HttpServletRequest request) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
    
            String token = request.getHeader("Authorization");
            if (token != null && !token.isEmpty()) {
                headers.set("Authorization", token);
            }
    
            NotificationDto notificationRequest = new NotificationDto(
                    event.getId(),
                    event.getUserProfile().getId(),
                    event.getUserProfile().getEmail(),
                    "You have an upcoming event: " + event.getName(),
                    event.getEventDate().atTime(0, 0)
            );
    
            HttpEntity<NotificationDto> requestNotif = new HttpEntity<>(notificationRequest, headers);
            ResponseEntity<Void> response = restTemplate.postForEntity(notificationUrl + "/create", requestNotif, Void.class);
    
            System.out.println("Notification creation response: " + response.getStatusCode());
            
        } catch (Exception e) {
            System.out.println("Failed to send notification: " + e.getMessage());
        }
    }
    
    /*private void updateNotification(Event event, HttpServletRequest request) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
    
            String token = request.getHeader("Authorization");
            if (token != null && !token.isEmpty()) {
                headers.set("Authorization", token);
            }
    
            NotificationDto notificationRequest = new NotificationDto(
                event.getId(),
                event.getUserProfile().getId(),
                event.getUserProfile().getEmail(),
                "You have an upcoming event: " + event.getName(),
                event.getEventDate().atTime(0, 0)
            );
    
            HttpEntity<NotificationDto> requestNotif = new HttpEntity<>(notificationRequest, headers);
            restTemplate.put(notificationUrl + "/update", requestNotif, Void.class);
    
        } catch (Exception e) {
            System.out.println("Failed to update notification: " + e.getMessage());
        }
    }*/
    

    private void deleteNotification(Long eventId, HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            HttpHeaders headers = new HttpHeaders();
            if (token != null && !token.isEmpty()) {
            headers.set("Authorization", token);  // Pass the token to the Notification Service
        }
            HttpEntity<Void> deleteRequest = new HttpEntity<>(headers);
            restTemplate.exchange(notificationUrl + "/delete/" + eventId, HttpMethod.DELETE, deleteRequest, Void.class);
            //String notifDeleteUrl = "http://notification-service:8082/notifications/delete/" + eventId;
            //restTemplate.exchange(notificationUrl + "/delete/" + eventId, HttpMethod.DELETE, null, Void.class);
        } catch (Exception e) {
            System.out.println("Failed to delete notification: " + e.getMessage());
        }
    } 
    

    //DTO class for the communication between Events and Notification Service
    static class NotificationDto {
        

        private Long eventId; //the event to which notification is associated
        private Long userId; //the user profile to which notification is associated 

        private String email; // email to send the notification to

        private String message;
        private LocalDateTime notificationDate;

        public NotificationDto(Long eventId, Long userId, String email, String message, LocalDateTime notificationDate) {
            this.eventId = eventId;
            this.userId = userId;
            this.email = email;
            this.message = message;
            this.notificationDate = notificationDate;
        }

        public Long getEventId() {
            return eventId;
        }

        public void setEventId(Long eventId) {
            this.eventId = eventId;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public LocalDateTime getNotificationDate() {
            return notificationDate;
        }

        public void setNotificationDate(LocalDateTime notificationDate) {
            this.notificationDate = notificationDate;
        }

    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleValidationExceptions(Exception e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

}
