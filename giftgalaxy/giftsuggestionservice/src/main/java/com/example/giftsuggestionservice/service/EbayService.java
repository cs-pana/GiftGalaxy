package com.example.giftsuggestionservice.service;

import java.util.ArrayList;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import java.util.StringJoiner;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import com.example.giftsuggestionservice.dto.GiftRequestDTO;
import com.example.giftsuggestionservice.dto.GiftSuggestionDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;


@Service
public class EbayService {

    private static final Logger logger = LoggerFactory.getLogger(EbayService.class);

    private final RestTemplate restTemplate;

    @Value("${ebay.api.key}")
    private String ebayApiKey;


    @Value("${ebay.api.client_id}")
    private String clientId;

    @Value("${ebay.api.client_secret}")
    private String clientSecret;

    public EbayService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private String getAccessToken() {
        String tokenUrl = "https://api.ebay.com/identity/v1/oauth2/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(clientId, clientSecret); // Setta l'autenticazione Basic

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("scope", "https://api.ebay.com/oauth/api_scope");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        try{
            logger.info("Fetching access token from eBay API...");
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            tokenUrl,
            HttpMethod.POST,
            entity,
            new ParameterizedTypeReference<Map<String, Object>>() {}
    );

            if (response.getBody() != null && response.getBody().get("access_token") != null){
            return (String) response.getBody().get("access_token"); // Restituisce il token

            }else{
                 System.err.println("Error: No access_token in response: " + response.getBody());
             }
        } catch (Exception e){
            e.printStackTrace();
            System.err.println("Error while fetching access token: " + e.getMessage());
            
        }
        return null;
}



private String buildQueryFromGiftRequest(GiftRequestDTO giftRequest) {

    StringJoiner queryJoiner = new StringJoiner(" ");

     //Aggiungi gli interessi alla query
    // if (giftRequest.getInterests() != null && !giftRequest.getInterests().isEmpty()) {
      // queryJoiner.add(String.join(" ", giftRequest.getInterests()));
   // }
     // Only add "anniversary" if it's applicable
     if (giftRequest.getAge() > 20 && "Anniversary".equalsIgnoreCase(giftRequest.getEvent())) {
        queryJoiner.add("anniversary");
    }

    // Ensure there's a fallback keyword in case nothing gets added
    if (queryJoiner.length() == 0) {
        queryJoiner.add("gift"); // Add a default fallback term like "gift"
    }

    // Aggiungi altre informazioni (se necessario)
    //if (giftRequest.getEvent() != null) {
      //  queryJoiner.add(giftRequest.getEvent());
    //}

    //if (giftRequest.getProfession() != null) {
     //   queryJoiner.add(giftRequest.getProfession());
    //}

    //if (giftRequest.getAge() > 0) {
     //   queryJoiner.add(String.valueOf(giftRequest.getAge()));
    //}

    return queryJoiner.toString();
}


    public List<GiftSuggestionDTO> getGiftSuggestions(GiftRequestDTO giftRequest) {

        

        try {
            logger.info("Inizio creazione query per regalo per {}", giftRequest.getRecipientName());
            
            // Costruzione della query basata sui parametri di `giftRequest`
            String query = buildQueryFromGiftRequest(giftRequest);
            logger.info("Query generata: {}", query);
    
            // Ottieni il token di accesso da eBay
            String accessToken = getAccessToken();

            List<String> categoryIds = new ArrayList<>();
    
            // bambini fra i 9 e i 16
        if (giftRequest.getAge() < 16 && giftRequest.getAge() > 9) {
            categoryIds.add("220"); // toys and hobbies
            logger.info("Categoria aggiunta: Toys and Hobbies");
        }

        // regali per bambini sotto i 9 anni 
        if (giftRequest.getAge() < 9) {
            categoryIds.add("2984"); // Baby
            logger.info("Categoria aggiunta: Baby"); 
        }

        // donna, adulta, amante del fashion
        if (giftRequest.getAge() > 16 && "female".equalsIgnoreCase(giftRequest.getSex()) && giftRequest.getInterests() != null && giftRequest.getInterests().contains("fashion")) {
            categoryIds.add("281"); // health & beauty, clothing, shoes, accessories, jewelery
            logger.info("Categoria aggiunta: Jewelry");
        }

        // persona generica con più di 16 anni che ama la letteratura
        if (giftRequest.getAge() > 16 && giftRequest.getInterests().contains("literature")) {
            categoryIds.add("267"); // Books
            logger.info("Categoria aggiunta: Books");
        }

        // amanti dei videogames 
        if (giftRequest.getAge() > 12 && giftRequest.getInterests().contains("videogames")) {
            categoryIds.add("1249"); // videogames & consoles, consumers electr.
            logger.info("Categoria aggiunta: Videogames and Consoles");
        }

        // amanti della musica
        if (giftRequest.getAge() > 12 && giftRequest.getInterests().contains("music")) {
            categoryIds.add("619"); // instruments and music
            logger.info("Categoria aggiunta: Instruments");
        }

        // amanti dello sport
        if (giftRequest.getAge() > 12 && giftRequest.getInterests().contains("sport")) {
            categoryIds.add("64482");  //sport goods, memorabilia
            logger.info("Categoria aggiunta: Sport Goods");
        }

        // amanti del viaggio e della natura
        if (giftRequest.getAge() > 12 && giftRequest.getInterests().contains("travel")) {
            categoryIds.add("3252");  //travel, experiences, photo
            logger.info("Categoria aggiunta: Travel and Nature");
        }

        // amanti del cinema
        if (giftRequest.getAge() > 10 && giftRequest.getInterests().contains("cinema")) {
            categoryIds.add("11232");  //movies & tv, camera 
            logger.info("Categoria aggiunta: Movies and TV");
        }

        // Unire tutti gli ID di categoria in una singola stringa separata da virgole
        String joinedCategoryIds = String.join(",", categoryIds);

        // Costruzione dinamica dell'URL con UriComponentsBuilder per eBay
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl("https://api.ebay.com/buy/browse/v1/item_summary/search")
            .queryParam("q", query)
            .queryParam("limit", 10); // Limite di risultati

        // Se ci sono ID di categoria validi, aggiungili alla query
        if (!joinedCategoryIds.isEmpty()) {
            uriBuilder.queryParam("category_ids", joinedCategoryIds);
            logger.info("Categoria/e aggiunta/e alla query: {}", joinedCategoryIds);
        }

        String url = uriBuilder.toUriString();

        logger.info("Chiamata a eBay API: {}", url);

        // Impostazione delle intestazioni (compreso il token di accesso)
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Chiamata all'API eBay
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        logger.info("Risposta ricevuta da eBay API: {}", response.getBody());

        // Converti la risposta in una lista di `GiftSuggestionDTO`
        return parseEbayResponse(response.getBody());

    } catch (Exception e) {
        logger.error("Errore durante la generazione delle proposte regalo", e);
        throw e; // Puoi decidere di rilanciare l'eccezione o gestirla diversamente
    }
}

private List<GiftSuggestionDTO> parseEbayResponse(String response) {
    ObjectMapper objectMapper = new ObjectMapper(); // Usa Jackson per il parsing
    List<GiftSuggestionDTO> suggestions = new ArrayList<>(); // Lista che conterrà i suggerimenti

    try {
        JsonNode root = objectMapper.readTree(response); // Legge la risposta JSON
        JsonNode items = root.path("itemSummaries");  // Estrae l'array degli elementi

        for (JsonNode item : items) {
            // Estrai le informazioni desiderate per ogni oggetto
            String name = item.path("title").asText();  // Estrai il titolo
            String link = item.path("itemWebUrl").asText();  // Estrai il link al prodotto

            // Aggiungi un nuovo GiftSuggestionDTO alla lista
            suggestions.add(new GiftSuggestionDTO(name, link, "eBay"));
        }
    } catch (Exception e) {
        e.printStackTrace(); // Gestisce eventuali errori
    }

    return suggestions; // Restituisce la lista di suggerimenti
}

}
    

    
