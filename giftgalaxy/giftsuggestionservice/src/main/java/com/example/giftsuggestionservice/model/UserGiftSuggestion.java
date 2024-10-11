package com.example.giftsuggestionservice.model;

import jakarta.persistence.*;;

@Entity
@Table(name = "user_gift_suggestions")
public class UserGiftSuggestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String itemName;
    private String link;
    private String ageRange;
    private String sex;
    private String interest;
    private String profession;
    private String relationship;
    private String eventType;

    public UserGiftSuggestion() {}

    public UserGiftSuggestion(String itemName, String link, String ageRange, String sex, String interest, String profession, String relationship, String eventType) {
        this.itemName = itemName;
        this.link = link;
        this.ageRange = ageRange;
        this.sex = sex;
        this.interest = interest;
        this.profession = profession;
        this.relationship = relationship;
        this.eventType = eventType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getAgeRange() {
        return ageRange;
    }

    public void setAgeRange(String ageRange) {
        this.ageRange = ageRange;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

}
