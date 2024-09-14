package com.example.profileservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.profileservice.model.UserProfile;
import com.example.profileservice.repository.UserProfileRepository;

//for testing purposes
//create default profile for default user in authservice at runtime
@Configuration
public class DefaultProfileConfig {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Bean
    public ApplicationRunner defaultProfileInitializer() {
        return args -> {
            String defaultEmail = "user@example.com";
            String defaultUsername = "Default";
            String defaultSurname = "User";
            

            // Check if the default profile already exists by email
            if (userProfileRepository.findByEmail(defaultEmail).isEmpty()) {
                // Create the default profile
                UserProfile defaultProfile = new UserProfile();
                defaultProfile.setUsername(defaultUsername);
                defaultProfile.setSurname(defaultSurname);
                defaultProfile.setEmail(defaultEmail);
                defaultProfile.setUserId((long) 1);

                // Save the profile in the database
                userProfileRepository.save(defaultProfile);

                System.out.println("Default user profile created with email: " + defaultEmail);
            } else {
                System.out.println("Default profile for already exists.");
            }
        };
    }
}
