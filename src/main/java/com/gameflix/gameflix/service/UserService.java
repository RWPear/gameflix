package com.gameflix.gameflix.service;

import com.gameflix.gameflix.model.User;
import com.gameflix.gameflix.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean usernameExists(String username) {
        return userRepo.existsByUsername(username);
    }

    public User register(String username, String rawPassword) {
        String hash = passwordEncoder.encode(rawPassword);
        User u = new User();
        u.setUsername(username);
        u.setPasswordHash(hash);
        return userRepo.save(u);
    }

    public boolean login(String username, String rawPassword) {
        Optional<User> u = userRepo.findByUsername(username);
        return u.isPresent() && passwordEncoder.matches(rawPassword, u.get().getPasswordHash());
    }
}
