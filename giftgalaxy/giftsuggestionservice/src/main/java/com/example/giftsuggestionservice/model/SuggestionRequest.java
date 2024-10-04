package com.example.giftsuggestionservice.model;

import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;


@Data
@Entity
@Table(name = "suggestion_request")
public class SuggestionRequest {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String recipientName;
  private Integer recipientAge;
  private String recipientSex;
  private String recipientProfession;
  private String relationship;

  @ElementCollection
  private List<String> interest;  // List of interests
  private String typeOfEvent;

  public SuggestionRequest() {}

  public SuggestionRequest(Long userId, String recipientName, Integer recipientAge, String recipientSex,
                             String recipientProfession, String relationship, List<String> interest, String typeOfEvent) {
        this.recipientName = recipientName;
        this.recipientAge = recipientAge;
        this.recipientSex = recipientSex;
        this.recipientProfession = recipientProfession;
        this.relationship = relationship;
        this.interest = interest;
        this.typeOfEvent = typeOfEvent;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public Integer getRecipientAge() {
        return recipientAge;
    }

    public void setRecipientAge(Integer recipientAge) {
        this.recipientAge = recipientAge;
    }

    public String getRecipientSex() {
        return recipientSex;
    }

    public void setRecipientSex(String recipientSex) {
        this.recipientSex = recipientSex;
    }

    public String getRecipientProfession() {
        return recipientProfession;
    }

    public void setRecipientProfession(String recipientProfession) {
        this.recipientProfession = recipientProfession;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public List<String> getInterest() {
        return interest;
    }

    public void setInterest(List<String> interest) {
        this.interest = interest;
    }

    public String getTypeOfEvent() {
        return typeOfEvent;
    }

    public void setTypeOfEvent(String typeOfEvent) {
        this.typeOfEvent = typeOfEvent;
    }

    // Metodo toString()
    @Override
    public String toString() {
        return "SuggestionRequest{" +
                "id=" + id +
                ", recipientName='" + recipientName + '\'' +
                ", recipientAge=" + recipientAge +
                ", recipientSex='" + recipientSex + '\'' +
                ", recipientProfession='" + recipientProfession + '\'' +
                ", relationship='" + relationship + '\'' +
                ", interest=" + interest +
                ", typeOfEvent='" + typeOfEvent + '\'' +
                '}';
    }



  
}
