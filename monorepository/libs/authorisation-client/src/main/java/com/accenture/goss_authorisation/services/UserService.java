package com.accenture.goss_authorisation.services;

import com.accenture.goss_authorisation.entities.User;
import com.accenture.goss_authorisation.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // public UserService() {
    // store.add(new User(UUID.randomUUID().toString(), "Hirdanshu",
    // "hirdanshu.vij@accenture.com"));
    // store.add(new User(UUID.randomUUID().toString(), "Parth",
    // "parth@accenture.com"));
    // store.add(new User(UUID.randomUUID().toString(), "Angelo",
    // "angelo@accenture.com"));
    // }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    // To create a new user.
    public User createUser(User user) {
        user.setUserId((UUID.randomUUID().toString()));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
}
