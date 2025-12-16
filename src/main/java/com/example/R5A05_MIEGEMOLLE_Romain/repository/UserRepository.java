package com.example.R5A05_MIEGEMOLLE_Romain.repository;

import com.example.R5A05_MIEGEMOLLE_Romain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
