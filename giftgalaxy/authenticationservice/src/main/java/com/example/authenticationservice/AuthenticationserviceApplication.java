package com.example.authenticationservice;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@SpringBootApplication
public class AuthenticationserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthenticationserviceApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    //Initialize firebase for Google log in
    @Bean
    public FirebaseApp initializeFirebase() {
        try {

            //use the service account key from firebase (it's in the resources folder)
            ClassPathResource serviceAccount = new ClassPathResource("firebaseServiceAccountKey.json");

            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount.getInputStream()))
                .setDatabaseUrl("https://gift-galaxy-6baae.firebaseio.com")
                .build();

            if (FirebaseApp.getApps().isEmpty()) { 
                return FirebaseApp.initializeApp(options);
            } else {
                return FirebaseApp.getInstance();
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize Firebase", e);
        }
    }
}
