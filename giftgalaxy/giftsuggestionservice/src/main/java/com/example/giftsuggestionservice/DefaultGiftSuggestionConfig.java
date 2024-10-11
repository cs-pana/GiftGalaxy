package com.example.giftsuggestionservice;

import com.example.giftsuggestionservice.model.UserGiftSuggestion;
import com.example.giftsuggestionservice.repository.GiftSuggestionRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;

import java.util.Arrays;
import java.util.List;

@Configuration
public class DefaultGiftSuggestionConfig {

    @Autowired
    private GiftSuggestionRepository userGiftSuggestionRepository;

    @Bean
    public ApplicationRunner defaultGiftSuggestionInitializer() {
        return args -> {
            if (userGiftSuggestionRepository.count() == 0) {
                // List of default gift suggestions
                List<UserGiftSuggestion> defaultSuggestions = Arrays.asList(
                    new UserGiftSuggestion("Educational Science Kit", "https://www.amazon.com", "0-12", "not specified", "sport", "student", "family", "birthday"),
                    new UserGiftSuggestion("Soccer Ball", "https://www.amazon.com", "0-12", "male", "sport", "student", "friend", "birthday"),
                    new UserGiftSuggestion("Barbie Doll", "https://www.amazon.com", "0-12", "female", "fashion", "student", "family", "birthday"),
                    new UserGiftSuggestion("Board Game", "https://www.amazon.com", "0-12", "not specified", "videogames", "student", "family", "birthday"),
                    new UserGiftSuggestion("Art Supplies Set", "https://www.amazon.com", "0-12", "not specified", "cinema", "student", "family", "birthday"),
                    new UserGiftSuggestion("Teen Fiction Novel", "https://www.amazon.com", "13-18", "female", "literature", "student", "friend", "birthday"),
                    new UserGiftSuggestion("Video Game", "https://www.amazon.com", "13-18", "male", "videogames", "student", "family", "christmas"),
                    new UserGiftSuggestion("Sports Jersey", "https://www.amazon.com", "13-18", "male", "sport", "student", "friend", "anniversary"),
                    new UserGiftSuggestion("Makeup Kit", "https://www.amazon.com", "13-18", "female", "fashion", "student", "family", "birthday"),
                    new UserGiftSuggestion("Travel Backpack", "https://www.amazon.com", "19-25", "not specified", "travel", "student", "friend", "birthday"),
                    new UserGiftSuggestion("Concert Tickets", "https://www.amazon.com", "19-25", "not specified", "music", "worker", "girlfriend", "anniversary"),
                    new UserGiftSuggestion("Wireless Earbuds", "https://www.amazon.com", "19-25", "male", "music", "worker", "friend", "special-occasion"),
                    new UserGiftSuggestion("Travel Guidebook", "https://www.amazon.com", "19-25", "not specified", "travel", "worker", "boyfriend", "birthday"),
                    new UserGiftSuggestion("Smartwatch", "https://www.amazon.com", "26-35", "not specified", "cinema", "worker", "friend", "anniversary"),
                    new UserGiftSuggestion("Subscription Box", "https://www.amazon.com", "26-35", "female", "fashion", "worker", "wife", "christmas"),
                    new UserGiftSuggestion("Self-Help Book", "https://www.amazon.com", "26-35", "male", "literature", "worker", "employee", "special-occasion"),
                    new UserGiftSuggestion("Spa Gift Set", "https://www.amazon.com", "36+", "female", "wellness", "worker", "wife", "anniversary"),
                    new UserGiftSuggestion("Travel Guidebook", "https://www.amazon.com", "36+", "not specified", "travel", "worker", "wife", "anniversary"),
                    new UserGiftSuggestion("Soccer Shoes", "https://www.amazon.com", "0-12", "male", "sport", "student", "family", "birthday"),
                    new UserGiftSuggestion("Basketball Shorts", "https://www.amazon.com", "13-18", "male", "sport", "student", "friend", "special-occasion"),
                    new UserGiftSuggestion("Theatre Tickets", "https://www.amazon.com", "19-25", "not specified", "theatre", "worker", "friend", "birthday"),
                    new UserGiftSuggestion("Fashion Jacket", "https://www.amazon.com", "19-25", "female", "fashion", "worker", "boyfriend", "anniversary"),
                    new UserGiftSuggestion("Movie Poster", "https://www.amazon.com", "19-25", "not specified", "cinema", "student", "friend", "christmas"),
                    new UserGiftSuggestion("Acoustic Guitar", "https://www.amazon.com", "19-25", "male", "music", "student", "friend", "birthday"),
                    new UserGiftSuggestion("Travel Book", "https://www.amazon.com", "26-35", "not specified", "travel", "worker", "family", "special-occasion"),
                    new UserGiftSuggestion("Cinema Tickets", "https://www.amazon.com", "13-18", "not specified", "cinema", "student", "friend", "anniversary"),
                    new UserGiftSuggestion("Gaming Hoodie", "https://www.amazon.com", "13-18", "male", "videogames", "student", "family", "birthday"),
                    new UserGiftSuggestion("Gaming Headset", "https://www.amazon.com", "13-18", "male", "videogames", "student", "friend", "christmas"),
                    new UserGiftSuggestion("Fashion Sunglasses", "https://www.amazon.com", "19-25", "female", "fashion", "worker", "friend", "birthday"),
                    new UserGiftSuggestion("Classic Literature Book", "https://www.amazon.com", "26-35", "male", "literature", "worker", "family", "special-occasion"),
                    new UserGiftSuggestion("Music Festival Ticket", "https://www.amazon.com", "26-35", "not specified", "music", "worker", "friend", "special-occasion"),
                    new UserGiftSuggestion("Travel Jacket", "https://www.amazon.com", "26-35", "not specified", "travel", "worker", "friend", "birthday"),
                    new UserGiftSuggestion("Sports Hoodie", "https://www.amazon.com", "13-18", "male", "sport", "student", "friend", "birthday"),
                    new UserGiftSuggestion("DVD Movie", "https://www.amazon.com", "36+", "not specified", "cinema", "worker", "family", "christmas"),
                    new UserGiftSuggestion("Travel Duffle Bag", "https://www.amazon.com", "26-35", "male", "travel", "worker", "friend", "special-occasion"),
                    new UserGiftSuggestion("Theatre Brochure", "https://www.amazon.com", "36+", "not specified", "theatre", "worker", "wife", "anniversary"),
                    new UserGiftSuggestion("Fashion Hat", "https://www.amazon.com", "36+", "female", "fashion", "worker", "husband", "birthday"),
                    new UserGiftSuggestion("Console Game", "https://www.amazon.com", "26-35", "male", "videogames", "worker", "friend", "special-occasion")


                );
                
                // Save all default suggestions
                userGiftSuggestionRepository.saveAll(defaultSuggestions);
                System.out.println("Default gift suggestions added.");
            } else {
                System.out.println("Default gift suggestions already exist.");
            }
        };
    }
}
