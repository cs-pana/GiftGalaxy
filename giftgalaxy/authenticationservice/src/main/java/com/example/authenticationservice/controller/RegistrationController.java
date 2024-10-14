package com.example.authenticationservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.authenticationservice.dto.UserProfileDto;
import com.example.authenticationservice.model.User;
import com.example.authenticationservice.model.UserRepository;
import com.example.authenticationservice.webtoken.JwtService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@RestController
@CrossOrigin(origins = {"http://localhost:8081", "http://localhost:3000"})
public class RegistrationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RestTemplate restTemplate;

    // Sign-Up via email and password
    @PostMapping(value = "/signup", consumes = "application/json")
    public ResponseEntity<String> createUser(@Valid @RequestBody User user) {

        // Check if email already exists
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return new ResponseEntity<>("This email already exists.", HttpStatus.BAD_REQUEST);
        }

       

        if (!isValidEmail(user.getEmail())) {
            return new ResponseEntity<>("Invalid email format.", HttpStatus.BAD_REQUEST);
        }

        // Encrypt password and save the user
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        // Create user profile via profile service
        createUserProfile(savedUser);

        return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
    }

    // Google Sign-Up
    @PostMapping(value = "/google-signup", consumes = "application/json")
    public ResponseEntity<String> googleSignUp(@RequestBody Map<String, String> idTokenMap) {
        String idToken = idTokenMap.get("idToken");

        FirebaseToken decodedToken;
        try {
            // Verify Firebase token
            System.out.println("Verifying firebase token...\n");
            decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            System.out.println(decodedToken);
        } catch (FirebaseAuthException e) {
            System.out.println("Error verifiying token...\n");
            return new ResponseEntity<>("Invalid Firebase token", HttpStatus.UNAUTHORIZED);
        }

        // get user's info from firebase token
        String email = decodedToken.getEmail();
        String displayName = decodedToken.getName();
        String uid = decodedToken.getUid(); // firebase UID

        // Check if user already exists
        Optional<User> existingUser = userRepository.findByEmail(email);
        User user;

        //prepare claims for jwt token generation

        if (existingUser.isEmpty()) {
            // Create new user if they don't exist
            user = new User();
            user.setEmail(email);
            user.setUsername(displayName != null ? displayName : email);  // Use displayName or email
            user.setPassword(passwordEncoder.encode(uid));  // store a Firebase UID for security

            User savedUser = userRepository.save(user);

            // Create user profile via profile service
            createUserProfile(savedUser);

            Map<String, Object> claims = new HashMap<>();
            claims.put("username", email);
            claims.put("email", email);
            claims.put("userid", user.getId());
    
            //generate jwt token for user
            String token = jwtService.generateToken(claims, email);


            return new ResponseEntity<>(token, HttpStatus.CREATED);
        } else {
            // User already exists
            user = existingUser.get();

            Map<String, Object> claims = new HashMap<>();
            claims.put("username", email);
            claims.put("email", email);
            claims.put("userid", user.getId());
    
            //generate jwt token for user
            String token = jwtService.generateToken(claims, email);

            // Generate JWT token
            return new ResponseEntity<>(token, HttpStatus.OK);
        }
    }

    // Helper method to create user profile in profile service
    private void createUserProfile(User savedUser) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        //info needed to create profile
        UserProfileDto userProfileDto = new UserProfileDto(
            savedUser.getId(),
            savedUser.getUsername(),
            savedUser.getSurname(),
            savedUser.getEmail()
        );

        HttpEntity<UserProfileDto> request = new HttpEntity<>(userProfileDto, headers);
        
        //call profile service (running on another port)
        restTemplate.postForEntity("http://profile-service:8081/profiles/create", request, UserProfileDto.class);
    }
    
    // Validate Email format
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
