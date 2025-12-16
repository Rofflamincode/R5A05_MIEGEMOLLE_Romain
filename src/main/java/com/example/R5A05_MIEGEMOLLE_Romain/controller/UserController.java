package com.example.R5A05_MIEGEMOLLE_Romain.controller;

import com.example.R5A05_MIEGEMOLLE_Romain.dto.CreateUserRequest;
import com.example.R5A05_MIEGEMOLLE_Romain.model.Role;
import com.example.R5A05_MIEGEMOLLE_Romain.model.User;
import com.example.R5A05_MIEGEMOLLE_Romain.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository repo;

    public UserController(UserRepository repo) {
        this.repo = repo;
    }

    @PostMapping
    public User create(@RequestBody CreateUserRequest req) {
        User u = new User();
        u.setUsername(req.username());
        u.setPassword(req.password());
        u.setRole(Role.valueOf(req.role()));
        return repo.save(u);
    }

    @GetMapping
    public List<User> list() {
        return repo.findAll();
    }
}
