package com.example.R5A05_MIEGEMOLLE_Romain.dto;

public record CreateArticleRequest(
        Long authorId,
        String content
) {}