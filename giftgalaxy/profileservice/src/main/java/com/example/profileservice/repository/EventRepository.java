package com.example.profileservice.repository;

import com.example.profileservice.model.Event;
import com.example.profileservice.model.UserProfile;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByUserProfile(UserProfile userProfile); //all events of a user profile
    Optional<Event> findByIdAndUserProfile(Long id, UserProfile userProfile); //find an event by its id and associated userProfile
}
