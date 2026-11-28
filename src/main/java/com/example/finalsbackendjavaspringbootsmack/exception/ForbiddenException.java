package com.example.finalsbackendjavaspringbootsmack.exception;

// 403 Forbidden: not allowed to do this
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }
}
