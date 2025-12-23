package com.example.R5A05_MIEGEMOLLE_Romain.controller;

import com.example.R5A05_MIEGEMOLLE_Romain.dto.CreateArticleRequest;
import com.example.R5A05_MIEGEMOLLE_Romain.model.Article;
import com.example.R5A05_MIEGEMOLLE_Romain.model.User;
import com.example.R5A05_MIEGEMOLLE_Romain.repository.ArticleRepository;
import com.example.R5A05_MIEGEMOLLE_Romain.repository.UserRepository;
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
        User author = userRepo.findById(req.authorId()).orElseThrow();

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
}
