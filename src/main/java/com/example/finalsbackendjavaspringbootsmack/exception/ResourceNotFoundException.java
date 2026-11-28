package com.smack.securewebchatroom.exception;

// 404 Not Found: the record does not exist
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
