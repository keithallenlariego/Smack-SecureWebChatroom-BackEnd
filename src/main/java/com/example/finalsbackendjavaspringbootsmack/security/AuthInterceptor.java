package com.example.finalsbackendjavaspringbootsmack.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.finalsbackendjavaspringbootsmack.entity.User;
import com.example.finalsbackendjavaspringbootsmack.exception.UnauthorizedException;
import com.example.finalsbackendjavaspringbootsmack.repository.UserRepository;
import com.example.finalsbackendjavaspringbootsmack.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Runs before every /api request except register, login and health.
// This is the "Valid session?" check from the activity diagram.
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Browsers send a preflight OPTIONS request first. It carries no token.
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new UnauthorizedException("Please log in first.");
        }

        Long userId = authService.getUserId(header.substring(7));
        if (userId == null) {
            throw new UnauthorizedException("Your session has ended. Please log in again.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("Your session has ended. Please log in again."));

        // Controllers read this with @RequestAttribute("currentUser")
        request.setAttribute("currentUser", user);
        return true;
    }
}
