package com.example.giftsuggestionservice.repository;

//import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


//import com.example.giftsuggestionservice.model.Suggestion;
import com.example.giftsuggestionservice.model.UserGiftSuggestion;


@Repository
public interface GiftSuggestionRepository extends JpaRepository<UserGiftSuggestion, Long> {
   /* // find by id method
    Optional<Suggestion> findById(Long id);

    // find by fav, find by name of gift
    List<Suggestion> findByFavourite(Boolean favourite);
    List<Suggestion> findByNameOfGift(String name);*/

    //fetch suggestions based on the request parameters
    List<UserGiftSuggestion> findByAgeRangeAndSexAndInterestAndProfessionAndRelationshipAndEventType(
        String ageRange, String sex, String interest, String profession, String relationship, String eventType);
}