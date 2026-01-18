package com.example.R5A05_MIEGEMOLLE_Romain.controller;

import com.example.R5A05_MIEGEMOLLE_Romain.dto.*;
import com.example.R5A05_MIEGEMOLLE_Romain.model.Article;
import com.example.R5A05_MIEGEMOLLE_Romain.model.User;
import com.example.R5A05_MIEGEMOLLE_Romain.repository.ArticleRepository;
import com.example.R5A05_MIEGEMOLLE_Romain.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/articles")
public class ArticleController {

    private final ArticleRepository articleRepo;
    private final UserRepository userRepo;

    public ArticleController(ArticleRepository articleRepo, UserRepository userRepo) {
        this.articleRepo = articleRepo;
        this.userRepo = userRepo;
    }

    @PreAuthorize("hasRole('PUBLISHER')")
    @PostMapping
    public Article create(@RequestBody CreateArticleRequest req) {

        String username = com.example.R5A05_MIEGEMOLLE_Romain.security.SecurityUtils.currentUsername();
        User author = userRepo.findByUsername(username).orElseThrow();

        Article a = new Article();
        a.setAuthor(author);
        a.setContent(req.content());
        a.setPublishedAt(Instant.now());

        return articleRepo.save(a);
    }



    @GetMapping
    public List<?> list() {
        List<Article> articles = articleRepo.findAll();

        if (isAnonymous()) {
            return articles.stream().map(this::toPublic).toList();
        }
        if (hasRole("MODERATOR")) {
            return articles.stream().map(this::toModerator).toList();
        }
        return articles.stream().map(this::toPublisher).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return articleRepo.findById(id)
                .map(article -> {
                    if (isAnonymous()) return ResponseEntity.ok(toPublic(article));
                    if (hasRole("MODERATOR")) return ResponseEntity.ok(toModerator(article));
                    return ResponseEntity.ok(toPublisher(article));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("(hasRole('PUBLISHER') and @articleSecurity.isAuthor(#id))")
    @PutMapping("/{id}")
    public ResponseEntity<Article> update(
            @PathVariable Long id,
            @RequestBody UpdateArticleRequest req
    ) {
        return articleRepo.findById(id)
                .map(article -> {
                    article.setContent(req.content());
                    Article updated = articleRepo.save(article);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('MODERATOR') or (hasRole('PUBLISHER') and @articleSecurity.isAuthor(#id))")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!articleRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        articleRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('PUBLISHER') and @articleSecurity.isNotAuthor(#id)")
    @PostMapping("/{id}/like")
    public ResponseEntity<Article> likeArticle(@PathVariable Long id) {

        String username = com.example.R5A05_MIEGEMOLLE_Romain.security.SecurityUtils.currentUsername();

        if (username == null) {
            return ResponseEntity.status(401).build();
        }

        User user = userRepo.findByUsername(username).orElse(null);
        Article article = articleRepo.findById(id).orElse(null);

        if (article == null || user == null) {
            return ResponseEntity.notFound().build();
        }

        article.getDislikedBy().remove(user);
        article.getLikedBy().add(user);

        return ResponseEntity.ok(articleRepo.save(article));
    }

    @PreAuthorize("hasRole('PUBLISHER') and @articleSecurity.isNotAuthor(#id)")
    @PostMapping("/{id}/dislike")
    public ResponseEntity<Article> dislikeArticle(@PathVariable Long id) {

        String username = com.example.R5A05_MIEGEMOLLE_Romain.security.SecurityUtils.currentUsername();

        if (username == null) {
            return ResponseEntity.status(401).build(); // pas connecté
        }

        User user = userRepo.findByUsername(username).orElse(null);
        Article article = articleRepo.findById(id).orElse(null);

        if (article == null || user == null) {
            return ResponseEntity.notFound().build();
        }

        article.getLikedBy().remove(user);
        article.getDislikedBy().add(user);

        return ResponseEntity.ok(articleRepo.save(article));
    }
    private Authentication auth() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private boolean isAnonymous() {
        Authentication a = auth();
        return a == null || a instanceof AnonymousAuthenticationToken || !a.isAuthenticated();
    }

    private boolean hasRole(String role) {
        Authentication a = auth();
        if (a == null) return false;
        String wanted = "ROLE_" + role;
        return a.getAuthorities().stream().anyMatch(ga -> ga.getAuthority().equals(wanted));
    }

    private UserSummaryDTO toUserSummary(User u) {
        return new UserSummaryDTO(u.getId(), u.getUsername());
    }

    private ArticlePublicDTO toPublic(Article a) {
        return new ArticlePublicDTO(a.getId(), a.getPublishedAt(), a.getAuthor().getUsername(), a.getContent());
    }

    private ArticlePublisherDTO toPublisher(Article a) {
        return new ArticlePublisherDTO(
                a.getId(),
                a.getPublishedAt(),
                a.getAuthor().getUsername(),
                a.getContent(),
                a.getLikedBy().size(),
                a.getDislikedBy().size()
        );
    }

    private ArticleModeratorDTO toModerator(Article a) {
        var liked = a.getLikedBy().stream().map(this::toUserSummary).toList();
        var disliked = a.getDislikedBy().stream().map(this::toUserSummary).toList();

        return new ArticleModeratorDTO(
                a.getId(),
                a.getPublishedAt(),
                toUserSummary(a.getAuthor()),
                a.getContent(),
                liked,
                liked.size(),
                disliked,
                disliked.size()
        );
    }

}
