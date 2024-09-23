package com.example.authenticationservice.model;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

//import com.example.authenticationservice.controller.RegistrationController.UserProfileDto;

//import org.springframework.amqp.rabbit.annotation.RabbitListener;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository repository;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        
        //Optional<User> user = repository.findByUsername(username);
        Optional<User> user = repository.findByEmail(email);
        if (user.isPresent()) {

            var userObj = user.get();

            //List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
            return org.springframework.security.core.userdetails.User.builder()
                    .username(userObj.getEmail())
                    .password(userObj.getPassword())
                    //.authorities(authorities)
                    .build();
        }else{
            throw new UsernameNotFoundException(email);
        }
    }

  /*   @RabbitListener(queues = "user-profile-updates-queue")
    public void handleUserProfileUpdate(UserProfileDto userProfileDto) {
        // Trova l'utente nel database dell'auth service
        User user = repository.findByEmail(userProfileDto.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Aggiorna i dati dell'utente
        user.setUsername(userProfileDto.getUsername());
        user.setSurname(userProfileDto.getSurname());
        user.setEmail(userProfileDto.getEmail());

        // Salva i nuovi dati nel database
        repository.save(user);
    }
     */
}
