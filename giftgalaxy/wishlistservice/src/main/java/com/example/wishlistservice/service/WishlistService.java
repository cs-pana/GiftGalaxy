package com.example.wishlistservice.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.wishlistservice.dto.WishlistDto;
import com.example.wishlistservice.model.Wishlist;
import com.example.wishlistservice.repository.WishlistRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Service
public class WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    @PersistenceContext
    private EntityManager entityManager;


    //add item to wishlist from gift suggestion service - frontend
    @Transactional
    public Wishlist addWishlist(WishlistDto wishlistDto) {
        Wishlist wish = new Wishlist();
        wish.setGiftName(wishlistDto.getGiftName());
        wish.setGiftLink(wishlistDto.getGiftLink());
        wish.setRecipientName(wishlistDto.getRecipientName());
        wish.setEventType(wishlistDto.getEventType());
        wish.setUserId(wishlistDto.getUserId());
        Wishlist addedWishlist = wishlistRepository.save(wish);
        entityManager.flush();
 
        return addedWishlist;

    }

    public List<Wishlist> getWishlistForUser(Long userId) {
        return wishlistRepository.findByUserId(userId);
    }

    // Update wishlist item
    @Transactional
    public Wishlist updateWishlist(Long id, WishlistDto wishlistDto) {
        Optional<Wishlist> wishlistItem = wishlistRepository.findById(id);
        if (wishlistItem.isPresent()) {
            Wishlist wish = wishlistItem.get();
            wish.setGiftName(wishlistDto.getGiftName());
            wish.setGiftLink(wishlistDto.getGiftLink());
            wish.setRecipientName(wishlistDto.getRecipientName());
            wish.setEventType(wishlistDto.getEventType());
            Wishlist updatedWishlist = wishlistRepository.save(wish);
            entityManager.flush();
            return updatedWishlist;
        } else {
            throw new RuntimeException("Wishlist item not found");
        }
    }


    @Transactional
    public void deleteWishlist(Long id) {
        Optional<Wishlist> wishlistItem = wishlistRepository.findById(id);
        if (wishlistItem.isPresent()) {
            wishlistRepository.delete(wishlistItem.get());
        } else {
            throw new RuntimeException("Wishlist item not found");
        }
    }
    //delete item from gift suggestios wishlist
    
}
