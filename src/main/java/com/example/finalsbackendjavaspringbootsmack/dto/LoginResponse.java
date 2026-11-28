package com.example.finalsbackendjavaspringbootsmack.dto;

import com.example.finalsbackendjavaspringbootsmack.entity.User;

// What a successful login returns: the session token and the user.
// The frontend sends the token back as "Authorization: Bearer <token>".
public class LoginResponse {

    private final String token;
    private final User user;

    public LoginResponse(String token, User user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }
}
