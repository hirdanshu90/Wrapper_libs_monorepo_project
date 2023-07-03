package com.accenture.gossauthorisation.wrapperclasses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    public String generateToken(String username, String password) {
        UserDetails userDetails = customUserDetailService.loadUserByUsername(username);
        System.out.println(userDetails);
        if (userDetails != null && userDetails.getUsername().equals(username)) {
            return jwtHelper.generateToken(userDetails);
        }
        return null;
    }

    public String validateToken(String token, String username, String password) {
        String validatedUsername = jwtHelper.extractUsernameFromToken(token);
        System.out.println(validatedUsername);
        if (validatedUsername != null && validatedUsername.equals(username)) {
            UserDetails userDetails = customUserDetailService.loadUserByUsername(username);
            if (userDetails != null) {
                BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                if (passwordEncoder.matches(password, userDetails.getPassword())) {
                    if (jwtHelper.validateToken(token, userDetails)) {
                        return validatedUsername;
                    }
                }
            }
        }
        return null;
    }
}
