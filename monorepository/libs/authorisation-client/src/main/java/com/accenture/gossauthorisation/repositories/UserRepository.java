package com.accenture.gossauthorisation.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.accenture.gossauthorisation.entities.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    public Optional<User> findByEmail(String email);
}
