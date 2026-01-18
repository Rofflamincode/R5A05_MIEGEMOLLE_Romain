package com.example.R5A05_MIEGEMOLLE_Romain.controller;

import com.example.R5A05_MIEGEMOLLE_Romain.dto.CreateArticleRequest;
import com.example.R5A05_MIEGEMOLLE_Romain.dto.UpdateArticleRequest;
import com.example.R5A05_MIEGEMOLLE_Romain.model.Article;
import com.example.R5A05_MIEGEMOLLE_Romain.model.User;
import com.example.R5A05_MIEGEMOLLE_Romain.repository.ArticleRepository;
import com.example.R5A05_MIEGEMOLLE_Romain.repository.UserRepository;
import org.springframework.http.ResponseEntity;
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
    public List<Article> list() {
        return articleRepo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Article> getById(@PathVariable Long id) {
        return articleRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!articleRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        articleRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

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
}
