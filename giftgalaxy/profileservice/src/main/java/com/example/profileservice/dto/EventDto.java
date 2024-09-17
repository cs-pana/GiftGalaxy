package com.example.profileservice.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EventDto {

    private Long id;

    @NotBlank(message = "Event name cannot be blank")
    private String name;

    @NotNull(message = "Event date cannot be null")
    private LocalDate eventDate;
    
    private boolean notify;
    
}
