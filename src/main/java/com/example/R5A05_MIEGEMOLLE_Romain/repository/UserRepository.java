package com.example.R5A05_MIEGEMOLLE_Romain.repository;

import com.example.R5A05_MIEGEMOLLE_Romain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
}
