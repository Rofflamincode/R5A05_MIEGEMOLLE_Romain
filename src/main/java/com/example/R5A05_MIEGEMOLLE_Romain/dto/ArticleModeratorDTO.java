package com.example.R5A05_MIEGEMOLLE_Romain.dto;

import java.time.Instant;
import java.util.List;

public record ArticleModeratorDTO(
        Long id,
        Instant publishedAt,
        UserSummaryDTO author,
        String content,
        List<UserSummaryDTO> likedBy,
        long likeCount,
        List<UserSummaryDTO> dislikedBy,
        long dislikeCount
) {}
