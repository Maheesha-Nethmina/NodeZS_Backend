package com.example.NodeZS_Backend.Repository;

import com.example.NodeZS_Backend.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Helpful for checking duplicate emails
    boolean existsByEmail(String email);
    User findByEmail(String email); // Add this line
}