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
            } */

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
                   // .queryParam("keyword", query) 
                    .queryParam("apikey", apiKey)
                    .queryParam("locale", "*")
                    .queryParam("countryCode", "IT")
                    .queryParam("size", 10);
                    
                  
                    
                    //if (giftRequest.getAge() < 16){
                     //   uriBuilder.queryParam("classificationName", "Family");
                    //}

                    //if ("christmas".equalsIgnoreCase(giftRequest.getEvent())) {
                    //    uriBuilder.queryParam("classificationName", "Holiday");
                    //    logger.info("Aggiunti filtri per eventi natalizi.");
                        //query += " christmas";  Aggiunge la parola "Christmas" alla query di ricerca
                    //}
                    //tentativi 
                     if (giftRequest.getInterests().contains("music")) {
                       uriBuilder.queryParam("classificationName", "music");
                    } 
                    if (giftRequest.getInterests().contains("theatre")) {
                       uriBuilder.queryParam("classificationName", "theatre");
                    }
                    if (giftRequest.getInterests().contains("sport")) {
                        uriBuilder.queryParam("classificationName", "sport");
                    }
                    if (giftRequest.getInterests().contains("cinema")) {
                        uriBuilder.queryParam("classificationName", "film");
                        logger.info("Aggiunti filtri per eventi comici.");
                    }
                    if (giftRequest.getInterests().contains("literature")){
                        uriBuilder.queryParam("classificationName", "art");
                    }
                    if (giftRequest.getInterests().contains("videogames")){
                        uriBuilder.queryParam("classificationName", "sport");
                    }
                    if (giftRequest.getInterests().contains("travel")){
                        uriBuilder.queryParam("classificationName", "festival");
                    } 
                    if ("christmas".equalsIgnoreCase(giftRequest.getEvent())) {
                        uriBuilder.queryParam("keyword", "natale");
                    }
                    



                    String url = uriBuilder.toUriString();
                
                logger.info("Chiamata a TicketMaster API: {}", url);
                // Do the request to the API
                String response = restTemplate.getForObject(url, String.class);
                
                logger.info("Risposta ricevuta da TicketMaster API: {}", response);

                List<GiftSuggestionDTO> suggestions = parseTicketMasterResponse(response);

                // Se non ci sono eventi in Italia, fai una nuova chiamata per cercare in Europa
                if (suggestions.isEmpty()) {
                    logger.info("Nessun evento trovato in Italia, cercando eventi in Europa...");
                    // Rimuovi il parametro countryCode per cercare in Europa
                    uriBuilder.replaceQueryParam("countryCode"); // Rimuove il parametro countryCode (Italia)
                    //uriBuilder.queryParam("size", 10); // Imposta il limite di risultati a 10

                    url = uriBuilder.toUriString();
                    logger.info("Chiamata a TicketMaster API (Europa): {}", url);
                    response = restTemplate.getForObject(url, String.class);
                    suggestions = parseTicketMasterResponse(response);
                }

                // Ritorna i suggerimenti
                return suggestions;
                } catch(Exception e){

                    logger.error("errore durante la generazione delle proposte regalo", e);
                    throw e;

                }
            }

        
        
}
