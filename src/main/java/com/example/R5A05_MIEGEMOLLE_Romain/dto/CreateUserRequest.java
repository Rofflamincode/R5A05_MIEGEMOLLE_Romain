package com.example.R5A05_MIEGEMOLLE_Romain.dto;

public record CreateUserRequest(
        String username,
        String password,
        String role
) {}
