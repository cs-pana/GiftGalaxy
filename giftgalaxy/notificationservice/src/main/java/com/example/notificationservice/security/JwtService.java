package com.example.notificationservice.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

import java.util.ArrayList;
import java.util.Base64;



@Service
public class JwtService {

    @Value("${jwt.secret}") //defined in application.properties
    private String secretKey; 

    //get email from the token;
    public String extractEmail(String jwt) {
        Claims claims = getClaims(jwt);
        return claims.getSubject();
    }

    private SecretKey generateKey() {
        byte[] decodeKey = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(decodeKey);

    }

    private Claims getClaims(String jwt) {
        return Jwts.parser()
                .verifyWith(generateKey())
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }

    public Boolean validateToken(String jwt) {
        Claims claims = getClaims(jwt);
        if (claims.getSubject() != null) {
            return true;
        } else {
            System.out.println("Invalid token");
            return false;
        }
    }


    public Long extractUserId(String jwt) {
        Claims claims = getClaims(jwt);
        return Long.parseLong(claims.get("userid").toString());
    }  

     public UsernamePasswordAuthenticationToken getAuthentication(String jwt) {
        String email = extractEmail(jwt);
        return new UsernamePasswordAuthenticationToken(email, null, new ArrayList<>());
    }

}

