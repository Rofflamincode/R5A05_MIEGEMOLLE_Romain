package com.example.R5A05_MIEGEMOLLE_Romain.dto;

import java.time.Instant;

public record ArticlePublicDTO(Long id, Instant publishedAt, String author, String content) {}