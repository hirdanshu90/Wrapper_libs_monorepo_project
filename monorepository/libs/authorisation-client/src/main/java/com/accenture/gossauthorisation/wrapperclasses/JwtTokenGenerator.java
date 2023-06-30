package com.accenture.gossauthorisation.wrapperclasses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.accenture.gossauthorisation.security.JwtHelper;
import com.accenture.gossauthorisation.services.CustomUserDetailService;

@Component
public class JwtTokenGenerator {
    private JwtHelper jwtHelper;
    private CustomUserDetailService customUserDetailService;

    @Autowired
    public JwtTokenGenerator(JwtHelper jwtHelper, CustomUserDetailService customUserDetailService) {
        this.jwtHelper = jwtHelper;
        this.customUserDetailService = customUserDetailService;
    }

    public String generateToken(String username, String string) {
        UserDetails userDetails = customUserDetailService.loadUserByUsername(username);
        return jwtHelper.generateToken(userDetails);
    }

    public String validateToken(String token) {
        String username = jwtHelper.extractUsernameFromToken(token);

        if (username != null) {
            UserDetails userDetails = customUserDetailService.loadUserByUsername(username);

            if (jwtHelper.validateToken(token, userDetails)) {
                return username;
            }
        }

        return null;
    }
}
