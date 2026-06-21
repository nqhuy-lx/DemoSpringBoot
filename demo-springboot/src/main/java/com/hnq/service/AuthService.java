package com.hnq.service;

import com.hnq.dto.request.SignInRequest;
import com.hnq.dto.response.TokenResponse;
import com.hnq.exception.InvalidDataException;
import com.hnq.model.User;
import com.hnq.repository.UserRepository;
import com.hnq.util.TokenType;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager  authenticationManager;
    private final JwtService jwtService;

    public TokenResponse authenticate(SignInRequest request) {

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        var user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new UsernameNotFoundException("username or password incorrect"));
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .build();
    }

    public TokenResponse refresh(HttpServletRequest request) {
        String refreshToken = request.getHeader("x-token");
        log.info("x-token = {}", refreshToken);

        if( StringUtils.isBlank(refreshToken)) {
            throw new InvalidDataException("x-token is blank");
        }
        // extract user form token
        final String username = jwtService.extractUsername(refreshToken, TokenType.REFRESH_TOKEN);
        log.info("username = {}", username);
        // check db
        Optional<User> user = userRepository.findByUsername(username);
        log.info("userId = {}", user.get().getId());

        if(!jwtService.isValidToken(refreshToken, TokenType.REFRESH_TOKEN, user.get())){
            throw new InvalidDataException("token is invalid");
        }

        String accessToken = jwtService.generateToken(user.get());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.get().getId())
                .build();
    }
}
