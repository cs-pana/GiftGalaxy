package com.example.giftsuggestionservice.dto;

public class GiftSuggestionDTO {

    private String name;
    private String link;
    private String source;

    public GiftSuggestionDTO(String name, String link, String source) {
        this.name = name;
        this.link = link;
        this.source = source;
    }

    public String getName() {
        return name;
    }

    // Setter per name
    public void setName(String name) {
        this.name = name;
    }

    // Getter per link
    public String getLink() {
        return link;
    }

    // Setter per link
    public void setLink(String link) {
        this.link = link;
    }

    // Getter per source
    public String getSource() {
        return source;
    }

    // Setter per source
    public void setSource(String source) {
        this.source = source;
    }
    
}
