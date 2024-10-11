package com.example.giftsuggestionservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collections;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.giftsuggestionservice.dto.GiftRequestDTO;
import com.example.giftsuggestionservice.dto.GiftSuggestionDTO;
import com.example.giftsuggestionservice.model.UserGiftSuggestion;
import com.example.giftsuggestionservice.repository.GiftSuggestionRepository;
import com.example.giftsuggestionservice.service.GiftSuggestionService;



@RestController
@RequestMapping("/api/gift-suggestions")
@CrossOrigin(origins = "http://localhost:3000")
public class GiftSuggestionController {

    private final GiftSuggestionService giftSuggestionService;
    private final GiftSuggestionRepository giftSuggestionRepository;


    public GiftSuggestionController(GiftSuggestionService giftSuggestionService, GiftSuggestionRepository giftSuggestionRepository) {
        
        this.giftSuggestionService = giftSuggestionService;
        this.giftSuggestionRepository = giftSuggestionRepository;

    }

   
    @PostMapping
    public ResponseEntity<List<GiftSuggestionDTO>> getGiftSuggestions(@RequestBody GiftRequestDTO giftRequest) {

        try {
            System.out.println("Received gift request: " + giftRequest);
            List<GiftSuggestionDTO> suggestions = giftSuggestionService.getGiftSuggestions(giftRequest);
            return ResponseEntity.ok(suggestions);
        }catch (Exception e) {
        e.printStackTrace(); 
        System.out.println("Errore nel ricevimento dei suggerimenti");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(Collections.emptyList());
    }
    }

    @PostMapping("/user-suggest")
    public ResponseEntity<String> submitUserGiftSuggestion(@RequestBody UserGiftSuggestion suggestion) {
        try {

            // Check if a similar suggestion already exists
            if (isSuggestionExists(suggestion)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Duplicate gift suggestion.");
            }
            giftSuggestionRepository.save(suggestion);
            return ResponseEntity.status(HttpStatus.CREATED).body("Gift suggestion submitted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving gift suggestion.");
        }
    }

    private boolean isSuggestionExists(UserGiftSuggestion newSuggestion) {
        // Check for duplicates (case-insensitive)
        return giftSuggestionRepository.findAll().stream().anyMatch(existingSuggestion ->
                existingSuggestion.getItemName().equalsIgnoreCase(newSuggestion.getItemName()) &&
                existingSuggestion.getLink().equalsIgnoreCase(newSuggestion.getLink()) &&
                existingSuggestion.getAgeRange().equalsIgnoreCase(newSuggestion.getAgeRange()) &&
                existingSuggestion.getSex().equalsIgnoreCase(newSuggestion.getSex()) &&
                existingSuggestion.getInterest().equalsIgnoreCase(newSuggestion.getInterest()) &&
                existingSuggestion.getProfession().equalsIgnoreCase(newSuggestion.getProfession()) &&
                existingSuggestion.getRelationship().equalsIgnoreCase(newSuggestion.getRelationship()) &&
                existingSuggestion.getEventType().equalsIgnoreCase(newSuggestion.getEventType()));
    }

}
