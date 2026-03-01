package com.thesis.chatservice.exception;

public class UnauthorizedException extends ChatServiceException {

    public UnauthorizedException(String message) {
        super(message);
    }
}