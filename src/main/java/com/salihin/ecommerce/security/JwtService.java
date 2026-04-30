package com.salihin.ecommerce.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret-key}")
    private String secretKey;

    // 1. Helper method to get the signing key
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 2. Generate a token for a User
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername()) // The email
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24 hours
                .signWith(getSignInKey()) // Sign it!
                .compact(); // Build it into a String
    }

    // 3. Extract all claims (the payload) from a token
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey()) // Check the signature
                .build()
                .parseSignedClaims(token)
                .getPayload(); // Get the data inside
    }

    // 4. Extract just the username (email)
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // 5. Check if the token is valid (matches user and not expired)
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        boolean isExpired = extractAllClaims(token).getExpiration().before(new Date());
        return (username.equals(userDetails.getUsername())) && !isExpired;
    }

}
