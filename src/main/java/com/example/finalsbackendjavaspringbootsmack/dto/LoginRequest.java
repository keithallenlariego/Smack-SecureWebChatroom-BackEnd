package com.example.finalsbackendjavaspringbootsmack.dto;

// The JSON body of POST /api/auth/login (identifier is the email or the username)
public class LoginRequest {

    private String identifier;
    private String password;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
