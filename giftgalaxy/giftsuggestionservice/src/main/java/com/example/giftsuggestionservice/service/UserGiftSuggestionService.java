package com.example.giftsuggestionservice.service;

import com.example.giftsuggestionservice.dto.GiftRequestDTO;
import com.example.giftsuggestionservice.dto.GiftSuggestionDTO;
import com.example.giftsuggestionservice.model.UserGiftSuggestion;
import com.example.giftsuggestionservice.repository.GiftSuggestionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserGiftSuggestionService {

    private final GiftSuggestionRepository giftSuggestionRepository;

    public UserGiftSuggestionService(GiftSuggestionRepository giftSuggestionRepository) {
        this.giftSuggestionRepository = giftSuggestionRepository;
    }

    // fetch user suggestions based on the gift request criteria
    public List<GiftSuggestionDTO> getUserGiftSuggestions(GiftRequestDTO giftRequest) {
        List<UserGiftSuggestion> userSuggestions = giftSuggestionRepository.findByAgeRangeAndSexAndInterestAndProfessionAndRelationshipAndEventType(
                mapAgeToRange(giftRequest.getAge()),
                giftRequest.getSex(),
                giftRequest.getInterests().isEmpty() ? null : giftRequest.getInterests().get(0), // Assuming only one interest for simplicity
                giftRequest.getProfession(),
                giftRequest.getRelationship(),
                giftRequest.getEvent());

        List<GiftSuggestionDTO> userGiftSuggestionDTOs = new ArrayList<>();
        for (UserGiftSuggestion suggestion : userSuggestions) {
            userGiftSuggestionDTOs.add(new GiftSuggestionDTO(
                    suggestion.getItemName(),
                    suggestion.getLink(),
                    "UserSuggested"
            ));
        }

        return userGiftSuggestionDTOs;
    }

    // map age to age range
    private String mapAgeToRange(int age) {
        if (age >= 0 && age <= 12) {
            return "0-12";
        } else if (age >= 13 && age <= 18) {
            return "13-18";
        } else if (age >= 19 && age <= 25) {
            return "19-25";
        } else if (age >= 26 && age <= 35) {
            return "26-35";
        } else {
            return "36+";
        }
    }
}
