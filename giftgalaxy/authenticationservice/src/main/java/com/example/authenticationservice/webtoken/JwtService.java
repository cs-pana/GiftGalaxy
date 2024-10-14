package com.example.authenticationservice.webtoken;

//import org.springframework.security.core.userdetails.UserDetails;
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
//import java.util.HashMap;


@Service
public class JwtService {
    
    //issuing new token, validating...

    //Run the JwtSecretMakerTest program to generate new secret key
    private static final String SECRET = "B0405FA0EB37048A86C205D3E01FE97C31FCB78DEDF0E215447A40C61035E598C2CD314EBF7B5175CE73A25FDFAADBCDE2EE2FC0E65AE94A8C9EBD06A0CA12AC";
    private static final long VALIDITY = TimeUnit.MINUTES.toMillis(60); //VALID FOR 1 HOUR

    //Generate token function:
    //we will generate a token for each user after they login
    /*public String generateToken(UserDetails userDetails) {
        Map<String, String> claims = new HashMap<>();
        claims.put("username", userDetails.getUsername());
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
    }*/

    public String generateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                   .claims(claims)                 // add custom claims
                   .subject(subject)               // subject of the token (user email)
                   .issuedAt(Date.from(Instant.now()))   //when token is generated -> current date and time
                   .expiration(Date.from(Instant.now().plusMillis(VALIDITY)))  //each jwt has an expiration time
                   .signWith(generateKey())           //sign with the secret key SECRET: we have to convert it into a Secret Key object
                   .compact();                        // convert into JSON format
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

    public Long extractUserId(String jwt) {
        Claims claims = getClaims(jwt);
        return Long.parseLong(claims.get("userid").toString());
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
