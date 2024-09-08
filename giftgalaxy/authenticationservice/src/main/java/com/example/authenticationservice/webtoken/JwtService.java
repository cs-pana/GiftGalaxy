package com.example.authenticationservice.webtoken;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.util.Date;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Map;
import java.util.HashMap;


@Service
public class JwtService {
    
    //issuing new token, validating...

    //Run the JwtSecretMakerTest program to generate new secret key
    private static final String SECRET = "6CBCE21BACF0918BF5A2018074633A9AA402D97770A2FBEA096CBF78DEE425DE727900B9BF0461E89FAE86BDF06AEB8EB50A5B65B11BEE67AD40125D9B4E45E1";
    private static final long VALIDITY = TimeUnit.MINUTES.toMillis(30); //VALID FOR 30 MINUTES
    //Generate token function:
    //we will generate a token for each user after they login
    public String generateToken(UserDetails userDetails) {
        Map<String, String> claims = new HashMap<>();
        claims.put("username", userDetails.getUsername()); //**MAYBE CHANGE SO THAT THIS HAS THE USERNAME?? */
        return Jwts.builder()
                    .claims(claims)
            //subject of the token -> user
                    .subject(userDetails.getUsername())
            //when token is generated -> current date and time
                    .issuedAt(Date.from(Instant.now()))
            //each jwt has an expiration
                    .expiration(Date.from(Instant.now().plusMillis(VALIDITY)))
            //sign with the secret key SECRET: we have to convert it into a Secret Key object
                    .signWith(generateKey())
            //convert into json format
                    .compact();
    }

    private SecretKey generateKey() {
        byte[] decodeKey = Base64.getDecoder().decode(SECRET);
        return Keys.hmacShaKeyFor(decodeKey);

    }

    public String extractUsername(String jwt) {

        //parse the data from the given webtoken
        Claims claims = getClaims(jwt);
        return claims.getSubject();

    }

     private Claims getClaims(String jwt) {
        return Jwts.parser()
                .verifyWith(generateKey())
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
        }

    public boolean isTokenValid(String jwt) {
        try {
        Claims claims = getClaims(jwt);
        System.out.println("Token expiration: " + claims.getExpiration());
        return claims.getExpiration().after(Date.from(Instant.now()));
    } catch (Exception e) {
        System.out.println("Invalid token: " + jwt);
        return false;
    }

} }
