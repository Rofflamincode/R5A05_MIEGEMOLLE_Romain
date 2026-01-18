package com.example.R5A05_MIEGEMOLLE_Romain.dto;

import java.time.Instant;

public record ArticlePublisherDTO(
        Long id,
        Instant publishedAt,
        String author,
        String content,
        long likeCount,
        long dislikeCount
) {}
