package com.example.finalsbackendjavaspringbootsmack.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.finalsbackendjavaspringbootsmack.dto.LoginRequest;
import com.example.finalsbackendjavaspringbootsmack.dto.LoginResponse;
import com.example.finalsbackendjavaspringbootsmack.dto.RegisterRequest;
import com.example.finalsbackendjavaspringbootsmack.entity.User;
import com.example.finalsbackendjavaspringbootsmack.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // POST /api/auth/register
    @PostMapping("/register")
    public User register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    // POST /api/auth/login
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    // POST /api/auth/logout (needs the Authorization header)
    @PostMapping("/logout")
    public String logout(@RequestHeader("Authorization") String authorization) {
        authService.logout(authorization.substring(7));
        return "You are logged out.";
    }
}
