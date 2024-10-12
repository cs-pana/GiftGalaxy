package com.example.giftsuggestionservice.service;

import java.util.ArrayList;
import java.util.List;
//import java.util.StringJoiner;

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
    

     /*    private String buildQueryFromGiftRequest(GiftRequestDTO giftRequest) {
            StringJoiner queryJoiner = new StringJoiner(" ");
    
            // Add insterests to the query
            if ("christmas".equalsIgnoreCase(giftRequest.getEvent())) {
                queryJoiner.add("natale");
            }
             
           /* if (giftRequest.getInterests().contains("music")) {
                queryJoiner.add("music");
            }
            if (giftRequest.getInterests().contains("theatre")) {
                queryJoiner.add("teatro");
            }
            if (giftRequest.getInterests().contains("sport")) {
                queryJoiner.add("sport");
            }
            if (giftRequest.getAge() < 16){
                queryJoiner.add("per-tutta-la-famiglia");
            }
            if (giftRequest.getInterests().contains("travel")){
                queryJoiner.add("viaggi-evento");
            }
            if (giftRequest.getInterests().contains("videogames")){
                queryJoiner.add("sport/esport");
            }
            if (giftRequest.getInterests().contains("literature")){
                queryJoiner.add("letture");
            }
            if (giftRequest.getInterests().contains("cinema")){
                queryJoiner.add("festival/festival-del-cinema");
            } 

            // add other informations
            //queryJoiner.add(giftRequest.getProfession());
            //queryJoiner.add(giftRequest.getEvent());
            //queryJoiner.add(String.valueOf(giftRequest.getAge()));
            
            return queryJoiner.toString();
        }   */

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
                    List<GiftSuggestionDTO> allSuggestions = new ArrayList<>();

                    // Lista degli interessi supportati
                    List<String> interests = giftRequest.getInterests();

                    // Add "family" interest if age is under 15
                    if (giftRequest.getAge() < 15) {
                        logger.info("L'età è inferiore a 15 anni, aggiungo l'interesse 'family'");
                        if (interests == null) {
                            interests = new ArrayList<>();
                        }
                        interests.add("family");
                    }

                    // Fallback to default category ( music) if no interests are specified
                    if ((interests == null || interests.isEmpty()) && giftRequest.getAge() >= 15) {
                        logger.info("Nessun interesse specificato, utilizzo categoria di default (music)");
                            interests = new ArrayList<>();
                           interests.add("music"); // Default category
                       } 

                
                    // Esegui una query per ciascun interesse
                    for (String interest : interests) {
                        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl("https://app.ticketmaster.com/discovery/v2/events.json")
                                .queryParam("apikey", apiKey)
                                .queryParam("locale", "*")
                                .queryParam("countryCode", "IT")
                                .queryParam("size", 5); // Limita i risultati a 5 per ogni chiamata

                        
            
                        // Aggiungi parametri di interesse
                        switch (interest.toLowerCase()) {
                            case "music":
                            if(giftRequest.getAge() >= 14){
                                uriBuilder.queryParam("classificationName", "music");
                                break;
                            }
                            case "theatre":
                            if(giftRequest.getAge() >= 14){
                                uriBuilder.queryParam("classificationName", "theatre");
                                break;
                            }
                            case "sport":
                            if(giftRequest.getAge() >= 14){
                                uriBuilder.queryParam("classificationName", "sport");
                                break;
                            }
                            case "cinema":
                            if(giftRequest.getAge() >= 14){
                                uriBuilder.queryParam("classificationName", "film");
                                logger.info("Aggiunti filtri per eventi di cinema.");
                                break;
                            }
                            case "literature":
                            if(giftRequest.getAge() >= 14){
                                uriBuilder.queryParam("classificationName", "art");
                                break;
                            }
                            case "videogames":
                            if(giftRequest.getAge() >= 14){
                                uriBuilder.queryParam("classificationName", "sport");
                                break;
                            }
                            case "travel":
                            if(giftRequest.getAge() >= 14){
                                uriBuilder.queryParam("classificationName", "festival");
                                break;
                            }
                            case "family":
                                uriBuilder.queryParam("classificationName", "family");
                                logger.info("Aggiunti filtri per eventi 'family'.");
                                break;
                        }

                        
            
                        
            
                        // Costruisci l'URL e invia la richiesta
                        String url = uriBuilder.toUriString();
                        logger.info("Chiamata a TicketMaster API per interesse {}: {}", interest, url);
            
                        // Effettua la chiamata API
                        String response = restTemplate.getForObject(url, String.class);
                        logger.info("Risposta ricevuta da TicketMaster API: {}", response);
            
                        // Analizza la risposta e aggiungi i suggerimenti alla lista
                        List<GiftSuggestionDTO> suggestions = parseTicketMasterResponse(response);
                        allSuggestions.addAll(suggestions);
            
                        // Se non ci sono eventi per questo interesse in Italia, cerca in Europa
                        if (suggestions.isEmpty()) {
                            logger.info("Nessun evento trovato in Italia per interesse {}, cercando eventi in Europa...", interest);
                            uriBuilder.replaceQueryParam("countryCode"); // Rimuove il parametro countryCode (Italia)
                            url = uriBuilder.toUriString();
                            logger.info("Chiamata a TicketMaster API (Europa) per interesse {}: {}", interest, url);
                            response = restTemplate.getForObject(url, String.class);
                            suggestions = parseTicketMasterResponse(response);
                            allSuggestions.addAll(suggestions);
                        }
                    }
                    

                 
            
                    // Ritorna tutti i suggerimenti combinati
                    return allSuggestions;
                } catch (Exception e) {
                    logger.error("Errore durante la generazione delle proposte regalo", e);
                    throw e;
                }
            }

        
        
}
