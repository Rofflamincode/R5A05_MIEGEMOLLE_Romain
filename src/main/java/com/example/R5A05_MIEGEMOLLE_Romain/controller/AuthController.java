package com.example.R5A05_MIEGEMOLLE_Romain.controller;

import com.example.R5A05_MIEGEMOLLE_Romain.dto.LoginRequest;
import com.example.R5A05_MIEGEMOLLE_Romain.security.JwtDTO;
import com.example.R5A05_MIEGEMOLLE_Romain.security.TokenGenerator;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenGenerator tokenGenerator;

    public AuthController(AuthenticationManager authenticationManager,
                          TokenGenerator tokenGenerator) {
        this.authenticationManager = authenticationManager;
        this.tokenGenerator = tokenGenerator;
    }

    @PostMapping("/login")
    public JwtDTO login(@RequestBody LoginRequest request) {

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        UserDetails userDetails = (UserDetails) auth.getPrincipal();

        return new JwtDTO(
                tokenGenerator.generateJwtToken(auth),
                userDetails.getUsername(),
                userDetails.getAuthorities()
                        .stream()
                        .map(a -> a.getAuthority())
                        .toList()
        );

    }
}

