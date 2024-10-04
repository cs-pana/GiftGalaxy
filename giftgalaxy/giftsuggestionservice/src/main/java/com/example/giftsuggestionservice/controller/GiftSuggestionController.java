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
import com.example.giftsuggestionservice.service.GiftSuggestionService;



@RestController
@RequestMapping("/api/gift-suggestions")
@CrossOrigin(origins = "http://localhost:3000")
public class GiftSuggestionController {

    private final GiftSuggestionService giftSuggestionService;

    public GiftSuggestionController(GiftSuggestionService giftSuggestionService) {
        
        this.giftSuggestionService = giftSuggestionService;
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


}
