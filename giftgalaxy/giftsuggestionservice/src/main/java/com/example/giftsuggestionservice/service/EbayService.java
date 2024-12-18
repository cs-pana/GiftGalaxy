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






    public List<GiftSuggestionDTO> getGiftSuggestions(GiftRequestDTO giftRequest) {

        List<GiftSuggestionDTO> allGiftSuggestions = new ArrayList<>(); // Lista per aggregare tutti i risultati

        try {
            logger.info("Inizio creazione query per regalo per {}", giftRequest.getRecipientName());

            // Ottieni il token di accesso da eBay
            String accessToken = getAccessToken();
            logger.info("Access token ottenuto: {}", accessToken);

            // Lista delle categorie da utilizzare
            List<String> categoryIds = new ArrayList<>();

            // Logica per aggiungere categorie basate sull'età e sugli interessi
            //bambini fra 11 e 16 anni generico - ok 
            if (giftRequest.getAge() < 16 && giftRequest.getAge() > 11 ) {
                categoryIds.add("220"); // Giocattoli e modellismo
                logger.info("Categoria aggiunta: Giocattoli e modellismo");
            }

            // bambini tra 6 e 12 anni e amanti dei viaggi - ok
            if (giftRequest.getAge() < 12 && giftRequest.getAge() > 6 && giftRequest.getInterests().contains("travel")) {
                categoryIds.add("260988"); // Categoria per viaggi per bambini
                logger.info("Categoria aggiunta: Travel per bambini");
            }

            // Bambini tra 8 e 11 anni
            if (giftRequest.getAge() < 11 && giftRequest.getAge() >= 8) {
                categoryIds.add("2550"); // Giochi da tavolo
                logger.info("Categoria aggiunta: Giochi da tavolo per bambini tra 8 e 11 anni");
            }

            // Studenti tra i 6 e i 18 anni - ok
            if (giftRequest.getAge() >= 6 && giftRequest.getAge() <= 18 && giftRequest.getProfession().contains("student")) {
                categoryIds.add("27668"); // Categoria articoli per la scuola
                logger.info("Categoria aggiunta: articoli scolastici");
            }

            //studenti universitari -ok
            if (giftRequest.getAge() >= 16  && giftRequest.getProfession().contains("student")) {
                categoryIds.add("175672"); // computer portatili, laptop, notebook
                logger.info("categoria aggiunta: computer portatili..");
            }
            if (giftRequest.getAge() >= 16  && giftRequest.getProfession().contains("student")) {
                categoryIds.add("171485"); //tablet e e-book
                logger.info("categoria aggiunta: tablet e ebook");
            }


            //lavoratori - ok
            if (giftRequest.getAge() >= 16 && giftRequest.getAge() < 66 && giftRequest.getProfession().contains("worker")) {
                categoryIds.add("302"); // cancelleria
                logger.info("cat aggiunta: cancelleria");
            }

            // Bambini tra 0 e 1 anni - ok
            if (giftRequest.getAge() <= 1 && giftRequest.getAge() >= 0) {
                categoryIds.add("19070"); // Categoria giochi per neonati
                logger.info("Categoria aggiunta: Giochi per neonati");
            }

            // Bambini tra 1 e 2 anni - ok 
            if (giftRequest.getAge() <= 2 && giftRequest.getAge() >= 1) {
                categoryIds.add("19068"); // Categoria giochi per l'infanzia
                logger.info("Categoria aggiunta: Giochi per l'infanzia");
            }
            // Bambini tra 3 e 7 anni - ok 
            if (giftRequest.getAge() <= 7 && giftRequest.getAge() >= 3) {
                categoryIds.add("230"); // Categoria giochi per l'infanzia
                logger.info("Categoria aggiunta: peluches");
            }

            // Bambini tra 7 e 16 anni - libri - ok
            if (giftRequest.getAge() <= 16 && giftRequest.getAge() >= 7 && giftRequest.getInterests().contains("literature")) {
                categoryIds.add("182882"); // libri per ragazzi
                logger.info("Categoria aggiunta: libri per ragazzi");
            }

            // Bambina tra 2 e 16 anni, amante della moda - ok
            if (giftRequest.getAge() < 16 && giftRequest.getAge() >= 2 && ("female".equalsIgnoreCase(giftRequest.getSex()) && giftRequest.getInterests().contains("fashion"))) {
                categoryIds.add("11462"); // Categoria vestiti per bambine
                logger.info("Categoria aggiunta: Vestiti per bambine");
            }

            // Bambino tra 2 e 16 anni - moda - ok 
            if (giftRequest.getAge() < 16 && giftRequest.getAge() >= 2 && ("male".equalsIgnoreCase(giftRequest.getSex()) && giftRequest.getInterests().contains("fashion"))) {
                categoryIds.add("11452"); // Categoria vestiti per bambini
                logger.info("Categoria aggiunta: Vestiti per bambini");
            }

            // Persona con più di 16 anni, amante della letteratura - ok
            if (giftRequest.getAge() > 16 && giftRequest.getInterests().contains("literature")) {
                categoryIds.add("171228"); // Categoria libri
                logger.info("Categoria aggiunta: Books");
            }

            // Persona con più di 16 anni, amante del teatro - ok
            if (giftRequest.getAge() > 16 && giftRequest.getInterests().contains("theatre")) {
                categoryIds.add("34821"); // Categoria biglietti eventi d'arte teatro e cultura
                logger.info("Categoria aggiunta: Biglietti eventi d'arte teatro e cultura");
            }


            // Persona con più di 12 anni, amante dei videogames - ok 
            if (giftRequest.getAge() > 12 && giftRequest.getInterests().contains("videogames")) {
                categoryIds.add("139971"); // Consoles
                logger.info("Categoria aggiunta: Videogames e Console");
            }
            if (giftRequest.getAge() > 12 && giftRequest.getInterests().contains("videogames")) {
                categoryIds.add("139973"); // giochi
                logger.info("Categoria aggiunta: giochi");
            }
            if (giftRequest.getAge() > 12 && giftRequest.getInterests().contains("videogames")) {
                categoryIds.add("80183"); //cuffie
                logger.info("Categoria aggiunta: cuffie");
            }
            if (giftRequest.getAge() > 12 && giftRequest.getInterests().contains("videogames")) {
                categoryIds.add("33346"); //manga
                logger.info("Categoria aggiunta: manga");
            }
            
            
            // Persona con più di 16 anni, donna amante del fashion - ok 
            if (giftRequest.getAge() > 16 && ("female".equalsIgnoreCase(giftRequest.getSex()) && giftRequest.getInterests().contains("fashion"))) {
                categoryIds.add("11848"); 
                logger.info("categoria aggiunta: profumi da donna");
            }
            if (giftRequest.getAge() > 16 && ("female".equalsIgnoreCase(giftRequest.getSex()) && giftRequest.getInterests().contains("fashion"))) {
                categoryIds.add("50692"); 
                logger.info("parure bigiotteria");
            }
            if (giftRequest.getAge() > 16 && ("female".equalsIgnoreCase(giftRequest.getSex()) && giftRequest.getInterests().contains("fashion"))) {
                categoryIds.add("260010"); 
                logger.info("abbigliamento  e accessori donna");
            }
            if (giftRequest.getAge() > 16 && ("female".equalsIgnoreCase(giftRequest.getSex()) && giftRequest.getInterests().contains("fashion"))) {
                categoryIds.add("177659"); 
                logger.info("piastre e arricciacapelli");
            }

            // Persona con più di 16 anni, uomo amante del fashion - ok 
            if (giftRequest.getAge() > 16 && ("male".equalsIgnoreCase(giftRequest.getSex()) && giftRequest.getInterests().contains("fashion"))) {
                categoryIds.add("29585"); 
                logger.info("cat agg: profumi e dopobarba uomo");
            }
            if (giftRequest.getAge() > 16 && ("male".equalsIgnoreCase(giftRequest.getSex()) && giftRequest.getInterests().contains("fashion"))) {
                categoryIds.add("10290"); 
                logger.info("gioielli da uomo");
            }
            if (giftRequest.getAge() > 16 && ("male".equalsIgnoreCase(giftRequest.getSex()) && giftRequest.getInterests().contains("fashion"))) {
                categoryIds.add("260012"); 
                logger.info("abbigliamento  e accessori uomo");
            }



            // Persona con più di 12 anni, amante della musica - ok
            if (giftRequest.getAge() > 12 && giftRequest.getInterests().contains("music")) {
                categoryIds.add("11233"); // Musica cd e vinili
                logger.info("Categoria aggiunta: Musica cd e vinili");
            }
            if (giftRequest.getAge() > 12 && giftRequest.getInterests().contains("music")) {
                categoryIds.add("34814"); // concerti e festival
                logger.info("Categoria aggiunta: biglietti eventi");
            }
            if (giftRequest.getAge() > 12 && giftRequest.getInterests().contains("music")) {
                categoryIds.add("80077"); //cuffiette musica
                logger.info("Categoria aggiunta: cuffiette");
            }

            // Persona con più di 12 anni, amante dello sport - ok
            if (giftRequest.getAge() > 15 && giftRequest.getInterests().contains("sport")) {
                categoryIds.add("15273"); // palestra,fitnell,corsa,yoga
                logger.info("Categoria aggiunta: Sport e viaggi");
            }
            
            if (giftRequest.getAge() <15 && giftRequest.getInterests().contains("sport") && ("male".equalsIgnoreCase(giftRequest.getSex()))) {
                categoryIds.add("260967"); // abbigliamento sportivo bambino
                logger.info("Categoria aggiunta: abbigliamento sportivo bambino");
            }

            if (giftRequest.getAge() <15 && giftRequest.getInterests().contains("sport") && ("female".equalsIgnoreCase(giftRequest.getSex()))) {
                categoryIds.add("260974"); // abbigliamento sportivo bambina
                logger.info("Categoria aggiunta: abbigliamento sportivo bambina");
            }

            if (giftRequest.getAge() > 12 && giftRequest.getInterests().contains("sport")) {
                categoryIds.add("34856"); // biglietti eventi sportivi
                logger.info("Categoria aggiunta: biglietti eventi sportivi");
            }

            // Persona con più di 12 anni, amante del viaggio e della natura
            if (giftRequest.getAge() > 12 && giftRequest.getInterests().contains("travel")) {
                categoryIds.add("157967"); // valigeria
                logger.info("Categoria aggiunta: valigeria e accessori viaggi");
            }

            // Persona con più di 10 anni, amante del cinema
            if (giftRequest.getAge() > 10 && giftRequest.getInterests().contains("cinema")) {
                categoryIds.add("11232"); // Categoria film e TV
                logger.info("Categoria aggiunta: Movies and TV");
            }

            // Persona con più di 16 anni, donna, evento anniversario
            if (giftRequest.getAge() > 16 && "female".equalsIgnoreCase(giftRequest.getSex()) &&("anniversary".equalsIgnoreCase(giftRequest.getEvent()))) {
                categoryIds.add("177030"); // anelli 
                logger.info("Categoria aggiunta: anelli");
            }
            if (giftRequest.getAge() > 16 && "female".equalsIgnoreCase(giftRequest.getSex()) &&("anniversary".equalsIgnoreCase(giftRequest.getEvent()))) {
                categoryIds.add("491"); // diamanti e gemme
                logger.info("Categoria aggiunta: diamanti e gemme");
            }
            if (giftRequest.getAge() > 16 && "female".equalsIgnoreCase(giftRequest.getSex()) &&("anniversary".equalsIgnoreCase(giftRequest.getEvent()))) {
                categoryIds.add("1305"); // biglietti eventi
                logger.info("Categoria aggiunta: biglietti eventi");
            }

            
            if (giftRequest.getAge() > 16 && "male".equalsIgnoreCase(giftRequest.getSex()) &&("anniversary".equalsIgnoreCase(giftRequest.getEvent()))) {
                categoryIds.add("10290"); // bracciali uomo
                logger.info("Categoria aggiunta: bracciali uomo");
            }
            if (giftRequest.getAge() > 16 && "male".equalsIgnoreCase(giftRequest.getSex()) &&("anniversary".equalsIgnoreCase(giftRequest.getEvent()))) {
                categoryIds.add("1305"); // biglietti eventi
                logger.info("Categoria aggiunta: biglietti eventi");
            }


            if (giftRequest.getAge() > 16 && "female".equalsIgnoreCase(giftRequest.getSex()) &&("christmas".equalsIgnoreCase(giftRequest.getEvent()))) {
                categoryIds.add("259703"); // tessili natale
                logger.info("Categoria aggiunta: tessili natale");
            }
            if (giftRequest.getAge() > 16 && "female".equalsIgnoreCase(giftRequest.getSex()) &&("christmas".equalsIgnoreCase(giftRequest.getEvent()))) {
                categoryIds.add("168138"); // candele natalizie
                logger.info("Categoria aggiunta: candele natalizie");
            }
        
            if (giftRequest.getAge() > 16 && "female".equalsIgnoreCase(giftRequest.getSex()) &&("christmas".equalsIgnoreCase(giftRequest.getEvent()))) {
                categoryIds.add("52346"); // cuscini natalizi
                logger.info("Categoria aggiunta: cuscini natalizi");
            }
            
            if (giftRequest.getAge() > 16  &&("christmas".equalsIgnoreCase(giftRequest.getEvent()))) {
                categoryIds.add("156890"); 
                logger.info("Categoria aggiunta: ");
            }
            
            if (giftRequest.getAge() > 16  &&("christmas".equalsIgnoreCase(giftRequest.getEvent()))) {
                categoryIds.add("177743"); // palle natale
                logger.info("Categoria aggiunta: palle natale");
            }

            if (giftRequest.getAge() > 30  &&"female".equalsIgnoreCase(giftRequest.getSex())) {
                categoryIds.add("45246"); //occhiali sole donna
                logger.info("Categoria aggiunta: occhiali sole donna");
            }
            if (giftRequest.getAge() > 60  &&"female".equalsIgnoreCase(giftRequest.getSex())) {
                categoryIds.add("353"); // arte e antiquariato
                logger.info("Categoria aggiunta: arte e antiquariato");
            }
            if (giftRequest.getAge() > 60  &&"female".equalsIgnoreCase(giftRequest.getSex())) {
                categoryIds.add("101415"); // vasi
                logger.info("Categoria aggiunta: vasi");
            }

            if (giftRequest.getAge() > 60 ) {
                categoryIds.add("116183"); // oggetti per occhiali
                logger.info("Categoria aggiunta: copri occhiali");
            }
            if (giftRequest.getAge() > 60 && "male".equalsIgnoreCase(giftRequest.getSex())) {
                categoryIds.add("79720"); // occhiali uomo
                logger.info("Categoria aggiunta: occhiali sole uomo");
            }
            if (giftRequest.getAge() > 60 && "male".equalsIgnoreCase(giftRequest.getSex())) {
                categoryIds.add("179836"); // vini
                logger.info("Categoria aggiunta: vini ");
            }

            if("corporate-event".equalsIgnoreCase(giftRequest.getEvent())){
                categoryIds.add("80183"); //cuffie
            }
            if("corporate-event".equalsIgnoreCase(giftRequest.getEvent())){
                categoryIds.add("23160"); //mouse
            }
            if("corporate-event".equalsIgnoreCase(giftRequest.getEvent())){
                categoryIds.add("23895"); //mouse pad
            }
            if("corporate-event".equalsIgnoreCase(giftRequest.getEvent())){
                categoryIds.add("80077"); //cuffiette
            }

            if("employee".equalsIgnoreCase(giftRequest.getRelationship())){
                categoryIds.add("23895"); //mouse pad
            }
            if("employee".equalsIgnoreCase(giftRequest.getRelationship())){
                categoryIds.add("23160"); //mouse
            }
            if("employee".equalsIgnoreCase(giftRequest.getRelationship())){
                categoryIds.add("80183"); //cuffie
            }
            if("acquaitance".equalsIgnoreCase(giftRequest.getRelationship())  && giftRequest.getAge() > 16){
                categoryIds.add("80077"); //cuffiette per smartphone
            }
            if("acquaitance".equalsIgnoreCase(giftRequest.getRelationship()) && "male".equalsIgnoreCase(giftRequest.getSex()) && giftRequest.getAge() > 16){
                categoryIds.add("4250"); //accessori uomo
            }
            if("acquaitance".equalsIgnoreCase(giftRequest.getRelationship()) && "female".equalsIgnoreCase(giftRequest.getSex()) && giftRequest.getAge() > 16){
                categoryIds.add("11838"); //cura del corpo
            }




        
            // Esegui una chiamata API per ogni categoria
            for (String categoryId : categoryIds) {
                int limit = 20; // Default
                if (categoryId.equals("220")) { 
                limit = 5;}
                if (categoryId.equals("260988")) { 
                limit = 5;}
                if (categoryId.equals("2550")) { 
                limit = 5;}
                if (categoryId.equals("27668")) { 
                        limit = 5;}
                if (categoryId.equals("175672")) { 
                        limit = 5;}
                if (categoryId.equals("135221")) { 
                        limit = 4;}
                if (categoryId.equals("19070")) { 
                        limit = 5;}
                if (categoryId.equals("19068")) { 
                    limit = 5;}
                if (categoryId.equals("182882")) { 
                        limit = 5;}
                if (categoryId.equals("11462")) { 
                        limit = 5;}
                if (categoryId.equals("11452")) { 
                        limit = 5;}
                if (categoryId.equals("34821")) { 
                        limit = 5;}
                if (categoryId.equals("1249")) { 
                        limit = 5;} 
                if (categoryId.equals("31786")) { 
                        limit = 5;}  
                if (categoryId.equals("10968")) { 
                        limit = 5;}   
                if (categoryId.equals("260010")) { 
                        limit = 5;}   
                if (categoryId.equals("29585")) { 
                            limit = 5;}
                if (categoryId.equals("10290")) { 
                            limit = 5;}
                if (categoryId.equals("260012")) { 
                            limit = 5;}
                if (categoryId.equals("11233")) { 
                            limit = 5;}
                if (categoryId.equals("888")) { 
                            limit = 5;} 
                if (categoryId.equals("3252")) { 
                            limit = 5;}  
                if (categoryId.equals("11232")) { 
                            limit = 5;}   
                if (categoryId.equals("177030")) { 
                            limit = 5;}  
                if (categoryId.equals("491")) { 
                            limit = 5;}  
                if (categoryId.equals("137853")) { 
                            limit = 5;}  
                if (categoryId.equals("31387")) { 
                            limit = 5;} 
                if (categoryId.equals("259703")) { 
                            limit = 5;} 
                if (categoryId.equals("168138")) { 
                            limit = 5;}  
                if (categoryId.equals("52346")) { 
                                limit = 5;}  
                if (categoryId.equals("156890")) { 
                                limit = 5;}  
                if (categoryId.equals("177743")) { 
                                limit = 5;}  
                if (categoryId.equals("45246")) { 
                                limit = 5;} 
                if (categoryId.equals("353")) { 
                                limit = 5;} 
                if (categoryId.equals("101415")) { 
                                limit = 5;} 
                if (categoryId.equals("116183")) { 
                                    limit = 5;} 
                if (categoryId.equals("79720")) { 
                                    limit = 5;}  
                if (categoryId.equals("179836")) { 
                                        limit = 5;}  
                if (categoryId.equals("80183")) { 
                                        limit = 5;}  
                if (categoryId.equals("23160")) { 
                                        limit = 5;}  
                if (categoryId.equals("23895")) { 
                                        limit = 5;} 
                if(categoryId.equals("10290")){
                    limit=5;
                }
                if(categoryId.equals("15273")){
                    limit =5;
                }
                if(categoryId.equals("139971")){
                    limit=5;
                }
                if(categoryId.equals("139973")){
                    limit = 5;
                }
                if(categoryId.equals("302")){
                    limit = 5;
                }
                if(categoryId.equals("171485")){
                    limit=5;
                }
                if(categoryId.equals("34814")){
                    limit=5;
                }
                if(categoryId.equals("34821")){limit=5;}
                if(categoryId.equals("34856")){limit=5;}
                if(categoryId.equals("80077")){limit=5;}
                if(categoryId.equals("139973")){limit=5;}
                if(categoryId.equals("246")){limit=5;}
                if(categoryId.equals("33346")){limit=5;}
                if(categoryId.equals("1305")){limit=8;}
                if(categoryId.equals("50692")){limit=5;}
                if(categoryId.equals("11848")){limit=5;}
                if(categoryId.equals("177659")){limit=5;}
                if(categoryId.equals("230")){limit=5;}
                if(categoryId.equals("171228")){limit=5;}
                if(categoryId.equals("157967")){limit=5;}
                if(categoryId.equals("260967")){limit=5;}
                if(categoryId.equals("260974")){limit=5;}
                
                
            

                
                // Creazione dell'URL con UriComponentsBuilder per ogni categoria
                UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl("https://api.ebay.com/buy/browse/v1/item_summary/search")
                        .queryParam("limit", limit) // Limite di risultati
                        .queryParam("category_ids", categoryId); // Aggiungi la categoria corrente

                


                String url = uriBuilder.toUriString();
                logger.info("Chiamata a eBay API per la categoria {}: {}", categoryId, url);

                // Impostazione delle intestazioni (compreso il token di accesso)
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + accessToken);
                headers.set("X-EBAY-C-MARKETPLACE-ID", "EBAY_IT");

                HttpEntity<String> entity = new HttpEntity<>(headers);

                // Chiamata all'API eBay
                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
                logger.info("Risposta ricevuta da eBay API: {}", response.getBody());

                // Converti la risposta in una lista di `GiftSuggestionDTO`
                List<GiftSuggestionDTO> giftSuggestions = parseEbayResponse(response.getBody());

                // Aggiungi i risultati alla lista totale
                allGiftSuggestions.addAll(giftSuggestions);
            }

        } catch (Exception e) {
            logger.error("Errore durante la generazione delle proposte regalo", e);
            throw e; // Puoi decidere di rilanciare l'eccezione o gestirla diversamente
        }

        return allGiftSuggestions;
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
    

    
