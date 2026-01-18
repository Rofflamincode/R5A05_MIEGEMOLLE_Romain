package com.example.R5A05_MIEGEMOLLE_Romain.security;

import com.example.R5A05_MIEGEMOLLE_Romain.repository.ArticleRepository;
import org.springframework.stereotype.Component;

@Component("articleSecurity")
public class ArticleSecurity {

    private final ArticleRepository articleRepo;

    public ArticleSecurity(ArticleRepository articleRepo) {
        this.articleRepo = articleRepo;
    }

    public boolean isAuthor(Long articleId) {
        String username = SecurityUtils.currentUsername();
        return articleRepo.findById(articleId)
                .map(a -> a.getAuthor().getUsername().equals(username))
                .orElse(false);
    }

    public boolean isNotAuthor(Long articleId) {
        return !isAuthor(articleId);
    }
}
