package com.hnq.service;

import com.hnq.dto.request.ForgotPasswordDTO;
import com.hnq.dto.request.ResetDTO;
import com.hnq.dto.request.ResetPasswordDTO;
import com.hnq.dto.request.SignInRequest;
import com.hnq.dto.response.TokenResponse;
import com.hnq.exception.InvalidDataException;
import com.hnq.exception.ResourceNotFoundException;
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
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

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

    public String forgotPassword(ForgotPasswordDTO request) {
        // check email exits
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new ResourceNotFoundException("email incorrect"));
        // user is activated
        if (!user.isAccountNonLocked())
            throw new InvalidDataException("user is locked");
        // generate token
        String resetToken = jwtService.generateResetToken(user);
        // send email
        String link = String.format("curl --location 'http://localhost:8080/auth/reset-password' \\\n" +
                "--header 'Content-Type: application/json' \\\n" +
                "--data '{\n" +
                "    \"secretKey\": \"%s\"\n" +
                "}'", resetToken);
        log.info("link = {}", link);
        return "Sent";
    }

    public String resetPassword(ResetDTO request) {
        User user = isValidUserByToken(request.getSecretKey());
        return "reset successfully";
    }

    public String changePassword(ResetPasswordDTO request) {
        User user = isValidUserByToken(request.getSecretKey());
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return "change";
    }

    private User isValidUserByToken(String token) {
        final String username = jwtService.extractUsername(token, TokenType.RESET_TOKEN);
        var user = userRepository.findByUsername(username).orElseThrow(() -> new InvalidDataException("token is invalid"));

        if (!jwtService.isValidToken(token, TokenType.RESET_TOKEN, user)) {
            throw new InvalidDataException("token is invalid");
        }
        return user;
    }
}
