package com.example.giftsuggestionservice.service;

import com.example.giftsuggestionservice.dto.GiftRequestDTO;
import com.example.giftsuggestionservice.dto.GiftSuggestionDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GiftSuggestionService {

    private final TicketMasterService ticketMasterService;
    private final EbayService ebayService;
    private final UserGiftSuggestionService userGiftSuggestionService;


    public GiftSuggestionService(TicketMasterService ticketMasterService, EbayService ebayService, UserGiftSuggestionService userGiftSuggestionService) {
        this.ticketMasterService = ticketMasterService;
        this.ebayService = ebayService;
        this.userGiftSuggestionService = userGiftSuggestionService;
    }

    public List<GiftSuggestionDTO> getGiftSuggestions(GiftRequestDTO giftRequest) {
        // obtain suggestions from ticketmaster
        List<GiftSuggestionDTO> ticketMasterSuggestions = ticketMasterService.getGiftSuggestions(giftRequest);

        // obtain suggestions from ebay 
        List<GiftSuggestionDTO> ebaySuggestions = ebayService.getGiftSuggestions(giftRequest); // Puoi filtrare con i suoi interessi

         // get users' suggestions
         List<GiftSuggestionDTO> userSuggestions = userGiftSuggestionService.getUserGiftSuggestions(giftRequest);

        // Combine the results 
        List<GiftSuggestionDTO> combinedSuggestions = new ArrayList<>();
        combinedSuggestions.addAll(ticketMasterSuggestions);
        combinedSuggestions.addAll(ebaySuggestions);
        combinedSuggestions.addAll(userSuggestions);


        return combinedSuggestions;
    }
}
