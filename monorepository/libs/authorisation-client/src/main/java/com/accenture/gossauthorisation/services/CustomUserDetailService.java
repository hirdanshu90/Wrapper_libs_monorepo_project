package com.accenture.gossauthorisation.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.accenture.gossauthorisation.entities.User;
import com.accenture.gossauthorisation.repositories.UserRepository;

@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Load user from db.
        User user = userRepository.findByEmail(username).orElseThrow(() -> new RuntimeException("User not found !"));
        return user;
    }
}
