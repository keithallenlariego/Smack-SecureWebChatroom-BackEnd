package com.example.finalsbackendjavaspringbootsmack.service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.finalsbackendjavaspringbootsmack.dto.LoginRequest;
import com.example.finalsbackendjavaspringbootsmack.dto.LoginResponse;
import com.example.finalsbackendjavaspringbootsmack.dto.RegisterRequest;
import com.example.finalsbackendjavaspringbootsmack.entity.User;
import com.example.finalsbackendjavaspringbootsmack.exception.BadRequestException;
import com.example.finalsbackendjavaspringbootsmack.exception.UnauthorizedException;
import com.example.finalsbackendjavaspringbootsmack.repository.UserRepository;
import com.example.finalsbackendjavaspringbootsmack.util.PasswordHasher;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    private static final Duration SESSION_TIMEOUT = Duration.ofMinutes(30);

    // token -> who it belongs to and when it was last used.
    // It lives in memory, so restarting the server logs everyone out.
    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

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

    // Log in (use case: Log In). Returns a token the client sends with every request.
    public LoginResponse login(LoginRequest request) {
        if (isBlank(request.getIdentifier()) || isBlank(request.getPassword())) {
            throw new BadRequestException("Enter your email or username and your password.");
        }

        String identifier = request.getIdentifier().trim();
        Optional<User> found = identifier.contains("@")
                ? userRepository.findByEmail(identifier)
                : userRepository.findByUsername(identifier);

        User user = found.orElseThrow(() -> new UnauthorizedException("Invalid email/username or password."));
        if (!PasswordHasher.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email/username or password.");
        }

        String token = UUID.randomUUID().toString();
        sessions.put(token, new Session(user.getUserId()));
        return new LoginResponse(token, user);
    }

    // Log out (use case: Log Out)
    public void logout(String token) {
        sessions.remove(token);
    }

    public boolean validateSession(String token) {
        return getUserId(token) != null;
    }

    // Returns the user id for a valid token, or null if the token is unknown or has expired.
    // Every valid use of a token keeps the session alive for another 30 minutes.
    public Long getUserId(String token) {
        Session session = sessions.get(token);
        if (session == null) {
            return null;
        }
        if (Duration.between(session.lastUsed, Instant.now()).compareTo(SESSION_TIMEOUT) > 0) {
            sessions.remove(token);
            return null;
        }
        session.lastUsed = Instant.now();
        return session.userId;
    }

    private boolean isBlank(String text) {
        return text == null || text.isBlank();
    }

    private static class Session {
        private final Long userId;
        private Instant lastUsed = Instant.now();

        private Session(Long userId) {
            this.userId = userId;
        }
    }
}
