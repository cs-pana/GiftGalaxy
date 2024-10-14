package com.example.authenticationservice.model;

import com.example.authenticationservice.dto.UserProfileDto;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;


@Service
public class ProfileUpdateListener {

    @Autowired
    private UserRepository userRepository;

    @RabbitListener(queues = "${rabbitmq.queue.profile.update}")
    public void handleProfileUpdate(UserProfileDto userProfileDto) {

        // Logica per aggiornare il database dell'Authentication Service
        System.out.println("Received profile update: " + userProfileDto);
        
        System.out.println("UserId: " + userProfileDto.getUserId());
       

       
        Optional<User> userOptional = userRepository.findById(userProfileDto.getUserId());

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Aggiorna i campi del profilo dell'utente
            user.setUsername(userProfileDto.getUsername());
            user.setEmail(userProfileDto.getEmail());
            user.setSurname(userProfileDto.getSurname());

            // Salva le modifiche nel database
            userRepository.save(user);
            System.out.println("User updated successfully: " + user);
        } else {
            System.out.println("User not found with ID: " + userProfileDto.getUserId());
        }

        
    }
}
