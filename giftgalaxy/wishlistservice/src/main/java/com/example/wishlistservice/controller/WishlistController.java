package com.example.wishlistservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.wishlistservice.dto.WishlistDto;
import com.example.wishlistservice.model.Wishlist;
import com.example.wishlistservice.repository.WishlistRepository;
import com.example.wishlistservice.security.JwtService;
import com.example.wishlistservice.service.WishlistService;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/wishlist")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private WishlistRepository wishlistRepository;

    @GetMapping("/{userId}")
    public ResponseEntity<List<Wishlist>> getUserWishlist(@PathVariable Long userId, HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
        String token = authorizationHeader.substring(7);
        Long jwtUserId = jwtService.extractUserId(token);
        
        // Verify if the userId from the path matches the userId from the JWT
        if (!userId.equals(jwtUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    
    List<Wishlist> wishlist = wishlistService.getWishlistForUser(userId);
    return ResponseEntity.ok(wishlist);
}

    @PostMapping("/add")
    public ResponseEntity<String> addToWishlist(@RequestBody WishlistDto wishlistDto) {

        wishlistService.addWishlist(wishlistDto);
        return ResponseEntity.ok("Wishlist service: added to wishlist successfully.");
    }

      // Update wishlist item
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateWishlist(@PathVariable Long id, @RequestBody WishlistDto wishlistDto, HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            Long jwtUserId = jwtService.extractUserId(token);

            Optional<Wishlist> item = wishlistRepository.findById(id);

            if (item.isPresent()) {
                Long wishlistUserId = item.get().getUserId();

                if (!jwtUserId.equals(wishlistUserId)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); 
            }
        
            wishlistService.updateWishlist(id, wishlistDto);
            return ResponseEntity.ok("Wishlist item updated successfully.");
        }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteFromWishlist(@PathVariable Long id, HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            Long jwtUserId = jwtService.extractUserId(token);

            Optional<Wishlist> item = wishlistRepository.findById(id);

            if (item.isPresent()) {
                Long wishlistUserId = item.get().getUserId();

                if (!jwtUserId.equals(wishlistUserId)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); 
            }
        wishlistService.deleteWishlist(id);
        return ResponseEntity.ok("Wishlist item deleted successfully.");
    }
}
    

