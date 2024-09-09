package com.example.authenticationservice;

import com.example.authenticationservice.model.User;
import com.example.authenticationservice.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;


//create default user at runtime

@Configuration
public class DefaultUserConfig {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Bean
    public ApplicationRunner defaultUserInitializer() {
        return args -> {
            String defaultEmail = "user@example.com";
            String defaultUsername = "defaultUser";
            String defaultPassword = "password"; 

            // Check if the default user already exists by email
            if (userRepository.findByEmail(defaultEmail).isEmpty()) {
                // Create the default user
                User defaultUser = new User();
                defaultUser.setUsername(defaultUsername);
                defaultUser.setEmail(defaultEmail);
                defaultUser.setPassword(passwordEncoder.encode(defaultPassword));

                // Save the user in the database
                userRepository.save(defaultUser);

                System.out.println("Default user created with email: " + defaultEmail);
            } else {
                System.out.println("Default user already exists.");
            }
        };
    }
}
