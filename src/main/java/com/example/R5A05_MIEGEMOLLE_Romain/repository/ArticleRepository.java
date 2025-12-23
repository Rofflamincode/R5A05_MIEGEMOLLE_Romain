package com.example.R5A05_MIEGEMOLLE_Romain.repository;

import com.example.R5A05_MIEGEMOLLE_Romain.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {
}
