package com.example.authenticationservice.controller;

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

import com.example.authenticationservice.webtoken.JwtService;
import com.example.authenticationservice.webtoken.LoginForm;

@RestController
public class LoginController {
      @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtService jwtService;


    /*@PostMapping(value = "/login", consumes = "application/json")
     public String authenticateAndGetToken(@RequestBody LoginForm loginForm) {
       // Print request body to console
       System.out.println("Received login request: email=" + loginForm.username() + ", password=" + loginForm.password());
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginForm.username(), loginForm.password()
        ));
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(userService.loadUserByUsername(loginForm.username()));
        } else {
            throw new UsernameNotFoundException("Invalid credentials");
        }
        //return jwtService.generateToken(userService.loadUserByUsername(loginForm.username()));
    }*/
     @PostMapping("/login")
    public ResponseEntity<String> authenticateAndGetToken(@RequestBody LoginForm loginForm) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginForm.username(), loginForm.password()
                )
        );

        if (authentication.isAuthenticated()) {
            // Generate JWT token
            String token = jwtService.generateToken((UserDetails) authentication.getPrincipal());

            // Return the token in the response body or header
            return ResponseEntity.ok()
                .header("Authorization", "Bearer " + token)  // Optionally include in the header
                .body(token);  // Return the token in the body
        } else {
            throw new BadCredentialsException("Invalid credentials");
        }
    }
}
