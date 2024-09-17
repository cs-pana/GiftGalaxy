package com.example.profileservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.profileservice.dto.UserProfileDto;
import com.example.profileservice.model.UserProfile;
import com.example.profileservice.repository.UserProfileRepository;

import java.util.NoSuchElementException;


@Service
public class UserProfileService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    public UserProfile createUserProfile(UserProfileDto userProfileDto) {

        //check if profile for user  already exists
        if (userProfileRepository.findByUserId(userProfileDto.getUserId()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Profile already created for user " + userProfileDto.getUserId());
        }

        //check if email already exists
        if (userProfileRepository.findByEmail(userProfileDto.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists " + userProfileDto.getEmail());
        }

        UserProfile profile = new UserProfile();
        profile.setUserId(userProfileDto.getUserId());
        profile.setEmail(userProfileDto.getEmail());
        profile.setUsername(userProfileDto.getUsername());
        profile.setSurname(userProfileDto.getSurname());



        return userProfileRepository.save(profile);

    }

    public UserProfileDto getUserProfile(Long userId) {
        UserProfile user = userProfileRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User not found with id " + userId));
        return new UserProfileDto(user.getId(), user.getUsername(), user.getSurname(), user.getEmail());
    }

    public UserProfileDto getUserProfileByEmail(String email) {
        UserProfile user = userProfileRepository.findByEmail(email)
            .orElseThrow(() -> new NoSuchElementException("User not found with email " + email));
        return new UserProfileDto(user.getId(), user.getUsername(), user.getSurname(), user.getEmail());
    }
}
    
