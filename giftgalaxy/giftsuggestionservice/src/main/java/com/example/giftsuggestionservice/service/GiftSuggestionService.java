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

    public GiftSuggestionService(TicketMasterService ticketMasterService, EbayService ebayService) {
        this.ticketMasterService = ticketMasterService;
        this.ebayService = ebayService;
    }

    public List<GiftSuggestionDTO> getGiftSuggestions(GiftRequestDTO giftRequest) {
        // obtain suggestions from ticketmaster
        List<GiftSuggestionDTO> ticketMasterSuggestions = ticketMasterService.getGiftSuggestions(giftRequest);

        // obtain suggestions from ebay 
        List<GiftSuggestionDTO> ebaySuggestions = ebayService.getGiftSuggestions(giftRequest); // Puoi filtrare con i suoi interessi

        // Combine the two results 
        List<GiftSuggestionDTO> combinedSuggestions = new ArrayList<>();
        combinedSuggestions.addAll(ticketMasterSuggestions);
        combinedSuggestions.addAll(ebaySuggestions);

        return combinedSuggestions;
    }
}
