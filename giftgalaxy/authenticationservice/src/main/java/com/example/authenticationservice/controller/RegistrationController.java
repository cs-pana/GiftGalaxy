package com.example.authenticationservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.authenticationservice.model.User;
import com.example.authenticationservice.model.UserRepository;

import jakarta.validation.Valid;

@RestController
public class RegistrationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping(value = "/signup", consumes = "application/json")
    public ResponseEntity<String> createUser(@Valid @RequestBody User user) {

        //check if email already exists
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return new ResponseEntity<>("This email already exists.", HttpStatus.BAD_REQUEST);
        }

       /* if (!isValidPassword(user.getPassword())) {
            return new ResponseEntity<>("Invalid password format", HttpStatus.BAD_REQUEST);
        }*/
        
        if (!isValidEmail(user.getEmail())) {
            return new ResponseEntity<>("Invalid email format.", HttpStatus.BAD_REQUEST);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
        
    }

     /* // Validate password 
     private boolean isValidPassword(String password) {
        //At least 6 characters, one uppercase, one lowercase, one number
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,}$";
        return password.matches(passwordPattern);
     }*/

    //Validate Email format
    private boolean isValidEmail(String email) {
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        return email.matches(emailPattern);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleValidationExceptions(Exception e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
    
}
