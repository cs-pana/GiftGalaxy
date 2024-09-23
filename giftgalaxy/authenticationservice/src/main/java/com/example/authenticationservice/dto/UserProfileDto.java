package com.example.authenticationservice.dto;
import java.io.Serializable;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long userId;
    private String username;
    private String surname;
    private String email;
    
}