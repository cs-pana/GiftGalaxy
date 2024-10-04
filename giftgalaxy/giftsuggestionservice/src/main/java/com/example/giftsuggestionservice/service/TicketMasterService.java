package com.example.giftsuggestionservice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.giftsuggestionservice.dto.GiftRequestDTO;
import com.example.giftsuggestionservice.dto.GiftSuggestionDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TicketMasterService {

    private static final Logger logger = LoggerFactory.getLogger(TicketMasterService.class);

    private final RestTemplate restTemplate;

    @Value("${ticketmaster.api.key}")
    private String apiKey;

    public TicketMasterService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public class TicketMasterEvent {
        private String name;
        private String link;

        public TicketMasterEvent(String name, String link, String ageRecommendation, String genre) {
            this.name = name;
            this.link = link;
        }

        public String getName() {
            return name;
        }
    
        public void setName(String name) {
            this.name = name;
        }
    
        // Getter e Setter per 'link'
        public String getLink() {
            return link;
        }
    
        public void setLink(String link) {
            this.link = link;
        }


        }

        private String buildQueryFromGiftRequest(GiftRequestDTO giftRequest) {
            StringJoiner queryJoiner = new StringJoiner(" ");
    
            // Add insterests to the query
            if (giftRequest.getInterests() != null && !giftRequest.getInterests().isEmpty()) {
            queryJoiner.add(String.join(" ", giftRequest.getInterests()));
            }
    
            // add other informations
            //queryJoiner.add(giftRequest.getProfession());
            //queryJoiner.add(giftRequest.getEvent());
            //queryJoiner.add(String.valueOf(giftRequest.getAge()));

            return queryJoiner.toString();
        }   

        //convert query response in a GiftSuggestionDTO list
        private List<GiftSuggestionDTO> parseTicketMasterResponse(String response) {
            ObjectMapper objectMapper = new ObjectMapper();
            List<GiftSuggestionDTO> suggestions = new ArrayList<>();

             try {
             JsonNode root = objectMapper.readTree(response);
             JsonNode events = root.path("_embedded").path("events");
        
             for (JsonNode event : events) {
                String name = event.path("name").asText();
                String link = event.path("url").asText();
                suggestions.add(new GiftSuggestionDTO(name, link, "TicketMaster"));
               }
             } catch (Exception e) {
                 
                    e.printStackTrace();
             }
    
            return suggestions;
            }

            public List<GiftSuggestionDTO> getGiftSuggestions(GiftRequestDTO giftRequest) {
                // build a query base of params
                try{
                    logger.info("Inizio creazione query per regalo per {}", giftRequest.getRecipientName());
                    String query = buildQueryFromGiftRequest(giftRequest);

                    //if (giftRequest.getAge() < 16) {
                     //   query += " family"; // Aggiungi "family" alla query se l'età è inferiore a 16
                    //}
                    logger.info("Query generata: {}", query);
                
                // build the URL for ticketmaster API
                UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl("https://app.ticketmaster.com/discovery/v2/events.json")
                    .queryParam("keyword", query) 
                    .queryParam("apikey", apiKey);
                    
                    if (giftRequest.getAge() < 16){
                        uriBuilder.queryParam("classificationName", "Family");
                    }

                    if ("christmas".equalsIgnoreCase(giftRequest.getEvent())) {
                        uriBuilder.queryParam("classificationName", "Holiday");
                        //query += " christmas";  Aggiunge la parola "Christmas" alla query di ricerca
                    }
                    

                    String url = uriBuilder.toUriString();
                
                logger.info("Chiamata a TicketMaster API: {}", url);
                // Do the request to the API
                String response = restTemplate.getForObject(url, String.class);
                
                logger.info("Risposta ricevuta da TicketMaster API: {}", response);

                // convertire il risultato in GiftSuggestionDTO
                return parseTicketMasterResponse(response);
                } catch(Exception e){

                    logger.error("errore durante la generazione delle proposte regalo", e);
                    throw e;

                }
            }

        
        
}
