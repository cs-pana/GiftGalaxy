package com.example.wishlistservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WishlistDto {

    private String giftName;
    private String giftLink;
    private String recipientName;
    private String eventType;
    private Long userId;

}
