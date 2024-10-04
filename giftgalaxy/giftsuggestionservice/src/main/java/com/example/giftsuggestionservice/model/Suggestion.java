package com.example.giftsuggestionservice.model;
import jakarta.persistence.*;

@Entity
@Table(name = "suggestion_result")
public class Suggestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // connect with SuggestionRequest
    @ManyToOne
    @JoinColumn(name = "suggestion_request_id", nullable = false)
    private SuggestionRequest suggestionRequest;
    private String nameOfGift;
    private String linkOfGift;
    private Boolean favourite;

    public Suggestion() {}

    public Suggestion(SuggestionRequest suggestionRequest, String nameOfGift, String linkOfGift, Boolean favourite) {
        this.suggestionRequest = suggestionRequest;
        this.nameOfGift = nameOfGift;
        this.linkOfGift = linkOfGift;
        this.favourite = favourite;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SuggestionRequest getSuggestionRequest() {
        return suggestionRequest;
    }

    public void setSuggestionRequest(SuggestionRequest suggestionRequest) {
        this.suggestionRequest = suggestionRequest;
    }

    public String getNameOfGift() {
        return nameOfGift;
    }

    public void setNameOfGift(String nameOfGift) {
        this.nameOfGift = nameOfGift;
    }

    public String getLinkOfGift() {
        return linkOfGift;
    }

    public void setLinkOfGift(String linkOfGift) {
        this.linkOfGift = linkOfGift;
    }

    public Boolean getFavourite() {
        return favourite;
    }

    public void setFavourite(Boolean favourite) {
        this.favourite = favourite;
    }

    // toString() method
    @Override
    public String toString() {
        return "Suggestion{" +
                "id=" + id +
                ", suggestionRequest=" + suggestionRequest +
                ", nameOfGift='" + nameOfGift + '\'' +
                ", linkOfGift='" + linkOfGift + '\'' +
                ", favourite=" + favourite +
                '}';
    }

    
}
