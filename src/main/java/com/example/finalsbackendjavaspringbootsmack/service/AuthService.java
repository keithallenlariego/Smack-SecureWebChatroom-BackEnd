package com.example.finalsbackendjavaspringbootsmack.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.finalsbackendjavaspringbootsmack.dto.RegisterRequest;
import com.example.finalsbackendjavaspringbootsmack.entity.User;
import com.example.finalsbackendjavaspringbootsmack.exception.BadRequestException;
import com.example.finalsbackendjavaspringbootsmack.repository.UserRepository;
import com.example.finalsbackendjavaspringbootsmack.util.PasswordHasher;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    // Register (use case: Register)
    public User register(RegisterRequest request) {
        if (isBlank(request.getUsername()) || isBlank(request.getEmail()) || isBlank(request.getPassword())) {
            throw new BadRequestException("Username, email and password are required.");
        }
        if (request.getPassword().length() < 8) {
            throw new BadRequestException("Password must be at least 8 characters.");
        }

        String username = request.getUsername().trim();
        String email = request.getEmail().trim();
        if (username.length() > 50 || email.length() > 100) {
            throw new BadRequestException("Username can be at most 50 characters and email at most 100.");
        }
        if (userRepository.existsByUsername(username)) {
            throw new BadRequestException("That username is already taken.");
        }
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("That email is already registered.");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(PasswordHasher.hash(request.getPassword()));
        user.setDisplayName(isBlank(request.getDisplayName()) ? username : request.getDisplayName().trim());
        user.setDashboardTheme("clay");
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    private boolean isBlank(String text) {
        return text == null || text.isBlank();
    }
}
