package com.smack.securewebchatroom.exception;

// 400 Bad Request: the request has a problem
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
