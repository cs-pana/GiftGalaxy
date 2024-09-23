package com.example.profileservice.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import com.example.profileservice.dto.UserProfileDto;
import org.springframework.stereotype.Service;


@Service
public class ProfileUpdateProducer {
    
    private final RabbitTemplate rabbitTemplate;

    // Inietti il nome della coda dal file application.properties
    @Value("${rabbitmq.queue.profile.update}")
    private String profileUpdateQueue;

    public ProfileUpdateProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendProfileUpdate(UserProfileDto userProfileDto) {
        try {
            System.out.println("Inviando aggiornamento profilo: " + userProfileDto);
            rabbitTemplate.convertAndSend(profileUpdateQueue, userProfileDto);
            System.out.println("Messaggio inviato alla coda: " + profileUpdateQueue);
        } catch (Exception e) {
            System.err.println("Errore durante l'invio del messaggio: " + e.getMessage());
        }
    }
}
