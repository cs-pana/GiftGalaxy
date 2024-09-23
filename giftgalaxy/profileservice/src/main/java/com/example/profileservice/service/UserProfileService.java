package com.example.profileservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;


import com.example.profileservice.dto.UserProfileDto;
import com.example.profileservice.model.UserProfile;
import com.example.profileservice.repository.UserProfileRepository;


import java.util.NoSuchElementException;


@Service
public class UserProfileService {

    @Autowired
    private UserProfileRepository userProfileRepository;
    //@Autowired
    //private RabbitTemplate rabbitTemplate;

    @Autowired
    private ProfileUpdateProducer profileUpdateProducer;

    //private final String EXCHANGE_NAME = "user-profile-updates-exchange";

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


        UserProfile savedProfile = userProfileRepository.save(profile);

        // Pubblica evento su RabbitMQ dopo aver creato il profilo
        profileUpdateProducer.sendProfileUpdate(userProfileDto);  // Pubblicazione su RabbitMQ

        return savedProfile;

    }
   
    public UserProfileDto getUserProfile(Long userId) {
        UserProfile user = userProfileRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User not found with id " + userId));
        return new UserProfileDto(user.getId(), user.getUsername(), user.getSurname(), user.getEmail());
    }

    
    public UserProfileDto getUserProfileByEmail(String email) {
        UserProfile user = userProfileRepository.findByEmail(email)
            .orElseThrow(() -> new NoSuchElementException("User not found with email " + email));
        return new UserProfileDto(user.getUserId(), user.getUsername(), user.getSurname(), user.getEmail());
    }

    // Metodo per aggiornare il profilo utente tramite email
    public UserProfile updateUserProfileByEmail(String email, UserProfileDto userProfileDto) {
        UserProfile userProfile = userProfileRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        
            // Aggiorna i dati del profilo
        userProfile.setUsername(userProfileDto.getUsername());
        userProfile.setSurname(userProfileDto.getSurname());
        userProfile.setEmail(userProfileDto.getEmail());

        
        System.out.println("Dati aggiornati per il profilo utente con email: " + email);
        
        // Salva i nuovi dati nel database
        UserProfile updatedProfile = userProfileRepository.save(userProfile);
        userProfileDto.setUserId(userProfile.getUserId());

        System.out.println("Profilo salvato nel database per l'email: " + email);
    
        //pubblica evento su rabbitmq
        profileUpdateProducer.sendProfileUpdate(userProfileDto);
        

        return updatedProfile;
    } 
}
    
