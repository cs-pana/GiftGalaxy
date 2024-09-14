package com.example.profileservice.repository;

import org.springframework.stereotype.Repository;

import com.example.profileservice.model.UserProfile;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByUserId(Long userId); //find user profile by user's id
    Optional<UserProfile> findByEmail(String email); //find user profile by email
}
