package com.smack.securewebchatroom.exception;

// 401 Unauthorized: wrong login details
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
