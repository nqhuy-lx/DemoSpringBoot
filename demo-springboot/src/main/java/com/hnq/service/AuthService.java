package com.hnq.service;

import com.hnq.dto.request.SignInRequest;
import com.hnq.dto.response.TokenResponse;
import com.hnq.exception.InvalidDataException;
import com.hnq.model.Token;
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
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenService tokenService;

    public TokenResponse authenticate(SignInRequest request) {

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        var user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new UsernameNotFoundException("username or password incorrect"));
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        // save db
        tokenService.saveToken(Token.builder()
                .username(user.getUsername())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .build();
    }

    public TokenResponse refresh(HttpServletRequest request) {
        String refreshToken = request.getHeader("r-token");
        log.info("r-token = {}", refreshToken);

        if (StringUtils.isBlank(refreshToken)) {
            throw new InvalidDataException("r-token is blank");
        }
        // extract user form token
        final String username = jwtService.extractUsername(refreshToken, TokenType.REFRESH_TOKEN);
        log.info("username = {}", username);
        // check db
        Optional<User> user = userRepository.findByUsername(username);
        log.info("userId = {}", user.get().getId());

        if (!jwtService.isValidToken(refreshToken, TokenType.REFRESH_TOKEN, user.get())) {
            throw new InvalidDataException("token is invalid");
        }

        String accessToken = jwtService.generateToken(user.get());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.get().getId())
                .build();
    }

    public String logout(HttpServletRequest request) {
        String refreshToken = request.getHeader("a-token");
        log.info("a-token = {}", refreshToken);

        if (StringUtils.isBlank(refreshToken)) {
            throw new InvalidDataException("a-token is blank");
        }

        final String username = jwtService.extractUsername(refreshToken, TokenType.ACCESS_TOKEN);

        Token token = tokenService.getTokenByUsername(username);
        tokenService.deleteToken(token);
        return "Logout";
    }
}
