package com.example.profileservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.profileservice.dto.UserProfileDto;
import com.example.profileservice.model.UserProfile;
import com.example.profileservice.repository.UserProfileRepository;
import com.example.profileservice.service.UserProfileService;

@RestController
@RequestMapping("/profiles")
public class ProfileController {

    @Autowired
    private UserProfileService userProfileService;
    

    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileDto> getUserProfile(@PathVariable Long userId) {
        UserProfileDto userProfile = userProfileService.getUserProfile(userId);
        return ResponseEntity.ok(userProfile);
    }


    @PostMapping("/create")
    public ResponseEntity<UserProfile> createProfile(@RequestBody UserProfileDto userProfileDto) {
        UserProfile createdProfile = userProfileService.createUserProfile(userProfileDto);
        return new ResponseEntity<>(createdProfile, HttpStatus.CREATED);
    }
}
