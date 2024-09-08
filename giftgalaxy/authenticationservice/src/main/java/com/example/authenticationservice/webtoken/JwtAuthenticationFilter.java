package com.example.authenticationservice.webtoken;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.authenticationservice.model.UserService;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        //Get the "Authorization" header from request
        String authHeader = request.getHeader("Authorization");
        //if authorization header doesn't match requirements, just ignore the request
        System.out.println("Request URL: " + request.getRequestURL());
        System.out.println("Authorization Header: " + authHeader);
    
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;

        }

        //get the token from the header
        String jwt = authHeader.substring(7);

        //parse username from token
        String username = jwtService.extractUsername(jwt);

        System.out.println("JWT Token: " + jwt);
System.out.println("Username extracted: " + username);


        //check if it is not null and also if it is not already authenticated
        if (username!=null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userService.loadUserByUsername(username);

            //check if token is still valid
            if (jwtService.isTokenValid(jwt)) {
                //token valid - we manually produce username password token
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
                );
                //we want to track who is logging into the system
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                System.out.println("OUTPUT: Setting authentication in SecurityContextHolder with user: " + authenticationToken.getPrincipal());
                System.out.println("OUTPUT: Authentication authorities: " + authenticationToken.getAuthorities());
    
                // Set the authentication in SecurityContextHolder

                //set security context as login
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);  
                
                System.out.println("OUTPUT: SecurityContextHolder authentication set: " + SecurityContextHolder.getContext().getAuthentication());



            }else {
                System.out.println("OUTPUT: JWT token is invalid");
            }
        } else {
            System.out.println("OUTPUT: Username is null or authentication already exists");
        }
        filterChain.doFilter(request, response);
    }



    
}
