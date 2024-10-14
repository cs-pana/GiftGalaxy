package com.example.authenticationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.authenticationservice.model.User;

@Repository
public interface AuthRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    
}
