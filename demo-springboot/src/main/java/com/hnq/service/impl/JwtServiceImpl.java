package com.hnq.service.impl;

import com.hnq.service.JwtService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtServiceImpl implements JwtService {
    @Override
    public String generateToken(UserDetails userDetails) {
        // generate token
        return "access token";
    }
}
