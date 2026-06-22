package com.hnq.service;

import com.hnq.util.TokenType;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String generateToken(UserDetails userDetails);

    String generateRefreshToken(UserDetails userDetails);

    String generateResetToken(UserDetails userDetails);

    String extractUsername(String token, TokenType type);

    boolean isValidToken(String token, TokenType type, UserDetails userDetails);
}
