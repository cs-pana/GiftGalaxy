package com.example.profileservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.profileservice.dto.EventDto;
import com.example.profileservice.dto.UserProfileDto;
import com.example.profileservice.model.Event;
import com.example.profileservice.model.UserProfile;
import com.example.profileservice.service.EventService;
import com.example.profileservice.service.UserProfileService;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

@RestController
@RequestMapping("/profiles")
public class ProfileController {

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private EventService eventService;
    

    /*@GetMapping("/{userId}")
    public ResponseEntity<UserProfileDto> getUserProfile(@PathVariable Long userId) {
        UserProfileDto userProfile = userProfileService.getUserProfile(userId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();  // The email is typically set as the username
        System.out.println("email = " + email);
        System.out.println(authentication);
        return ResponseEntity.ok(userProfile);
    }*/
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
        return ResponseEntity.status(HttpStatus.CREATED).body(newEvent);
    }

    @PutMapping("/me/events/{eventId}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long eventId, @RequestBody EventDto eventDto, HttpServletRequest request) {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Event updatedEvent = eventService.updateEventForUser(email, eventId, eventDto);
        return ResponseEntity.ok(updatedEvent);
    }

    @DeleteMapping("/me/events/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long eventId, HttpServletRequest request) {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        eventService.deleteEventForUser(email, eventId);
        return ResponseEntity.noContent().build();
    }
}
