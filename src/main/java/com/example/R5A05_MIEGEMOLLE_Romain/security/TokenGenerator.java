package com.example.R5A05_MIEGEMOLLE_Romain.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class TokenGenerator {

    @Value("${r5a05.app.jwtSecret}")
    private String jwtSecret;

    @Value("#{1000 * 60}")
    private long jwtExpirationMs;

    public String generateJwtToken(Authentication authentication) {

        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        Date tokenCreationDate = new Date();
        Date tokenExpirationDate = new Date(tokenCreationDate.getTime() + jwtExpirationMs);

        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(tokenCreationDate)
                .setExpiration(tokenExpirationDate)
                .signWith(key)
                .compact();

    }
}