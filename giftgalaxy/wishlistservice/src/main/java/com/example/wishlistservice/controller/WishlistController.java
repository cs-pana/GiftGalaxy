package com.example.wishlistservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.example.wishlistservice.service.WishlistService;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/wishlist")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @GetMapping("/{userId}")
    public List<Wishlist> getUserWishlist(@PathVariable Long userId) {
        List<Wishlist> wishlist = wishlistService.getWishlistForUser(userId);
        return wishlist;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addToWishlist(@RequestBody WishlistDto wishlistDto) {

        wishlistService.addWishlist(wishlistDto);
        return ResponseEntity.ok("Wishlist service: added to wishlist successfully.");
    }

      // Update wishlist item
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateWishlist(@PathVariable Long id, @RequestBody WishlistDto wishlistDto) {
        wishlistService.updateWishlist(id, wishlistDto);
        return ResponseEntity.ok("Wishlist item updated successfully.");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteFromWishlist(@PathVariable Long id) {
        wishlistService.deleteWishlist(id);
        return ResponseEntity.ok("Wishlist item deleted successfully.");
    }
}
    

