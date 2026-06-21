package com.hnq.service;

import com.hnq.exception.ResourceNotFoundException;
import com.hnq.model.Token;
import com.hnq.repository.TokenRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public record TokenService(TokenRepository tokenRepository) {
    public long saveToken(Token token) {
        Optional<Token> optionalToken = tokenRepository.findByUsername(token.getUsername());
        if (optionalToken.isEmpty()) {
            tokenRepository.save(token);
            return token.getId();
        } else {
            Token currentToken = optionalToken.get();
            currentToken.setAccessToken(token.getAccessToken());
            currentToken.setRefreshToken(token.getRefreshToken());
            tokenRepository.save(currentToken);
            return currentToken.getId();
        }
    }

    public void deleteToken(Token token) {
        tokenRepository.delete(token);
    }

    public Token getTokenByUsername(String username) {
        return tokenRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("Token not exist"));
    }
}
