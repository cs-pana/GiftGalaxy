package com.example.authenticationservice.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.authenticationservice.repository.AuthRepository;
import com.example.authenticationservice.webtoken.JwtService;
import com.example.authenticationservice.webtoken.LoginForm;

@RestController
public class LoginController {
    
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<String> authenticateAndGetToken(@RequestBody LoginForm loginForm) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginForm.username(), loginForm.password()
                )
        );

        if (authentication.isAuthenticated()) {
            // Generate JWT token
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            //String token = jwtService.generateToken((UserDetails) authentication.getPrincipal());
            // Prepare claims for JWT token
            Map<String, Object> claims = new HashMap<>();
            claims.put("username", userDetails.getUsername());
            claims.put("email", userDetails.getUsername());  //username is the email
            claims.put("userid", authRepository.findByEmail(userDetails.getUsername()).getId());

            String token = jwtService.generateToken(claims, userDetails.getUsername());
            // Return the token in the response body and header
            return ResponseEntity.ok()
                .header("Authorization", "Bearer " + token)
                .body(token);
        } else {
            throw new BadCredentialsException("Invalid credentials");
        }
    }
}