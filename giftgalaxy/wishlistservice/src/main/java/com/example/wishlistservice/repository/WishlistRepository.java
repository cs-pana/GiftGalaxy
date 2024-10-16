package com.example.wishlistservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.wishlistservice.model.Wishlist;


@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> { 
    List<Wishlist> findByUserId(Long userId);
}

