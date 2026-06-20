package com.hnq.controller;

import com.hnq.dto.request.SignInRequest;
import com.hnq.dto.response.TokenResponse;
import com.hnq.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j // log
@RestController
@RequestMapping("/auth")
@Tag(name = "Auth controller")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid  @RequestBody SignInRequest signInRequest) {
        return new ResponseEntity<>(authService.authenticate(signInRequest), HttpStatus.OK);
    }

    @PostMapping("/logout")
    public String logout() {
        return "logout";
    }

    @PostMapping("/refresh")
    public String refresh() {
        return "refresh";
    }

}
