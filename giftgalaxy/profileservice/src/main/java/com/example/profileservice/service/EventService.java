package com.example.profileservice.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.profileservice.repository.EventRepository;
import com.example.profileservice.repository.UserProfileRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import com.example.profileservice.dto.EventDto;
import com.example.profileservice.model.UserProfile;
import com.example.profileservice.model.Event;


@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<EventDto> getEventsForUser(String email) {
        UserProfile userProfile = userProfileRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        List<Event> events = eventRepository.findByUserProfile(userProfile);
        return events.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Transactional
    public Event createEventForUser(String email, EventDto eventDto) {
        UserProfile userProfile = userProfileRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        Event event = new Event();
        event.setName(eventDto.getName());
        event.setEventDate(eventDto.getEventDate());
        event.setNotify(eventDto.isNotify());
        event.setUserProfile(userProfile);
        Event savedEvent = eventRepository.save(event);
        entityManager.flush(); // Ensures the event is committed to the database immediately

        return savedEvent;
    }

    @Transactional
    public Event updateEventForUser(String email, Long eventId, EventDto eventDto) {
        UserProfile userProfile = userProfileRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId));

        if (!event.getUserProfile().equals(userProfile)) {
            throw new RuntimeException("Unauthorized action for this event");
        }

        event.setName(eventDto.getName());
        event.setEventDate(eventDto.getEventDate());
        event.setNotify(eventDto.isNotify());

        Event savedEvent = eventRepository.save(event);
        entityManager.flush(); // Ensures the event is committed to the database immediately

        return savedEvent;
    }

    @Transactional
    public void deleteEventForUser(String email, Long eventId) {
        UserProfile userProfile = userProfileRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId));

        if (!event.getUserProfile().equals(userProfile)) {
            throw new RuntimeException("Unauthorized action for this event");
        }

        eventRepository.delete(event);
    }



    private EventDto convertToDto(Event event) {
        EventDto dto = new EventDto();
        dto.setId(event.getId());
        dto.setName(event.getName());
        dto.setEventDate(event.getEventDate());
        dto.setNotify(event.isNotify());
        return dto;
    }
    
}
