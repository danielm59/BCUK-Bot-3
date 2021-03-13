package com.expiredminotaur.bcukbot.web.security;

public class AccessDeniedException extends RuntimeException
{
    public AccessDeniedException() {
    }

    public AccessDeniedException(String message) {
        super(message);
    }
}
