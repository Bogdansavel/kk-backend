package com.example.kkbackend.exception;

public class ActiveRoundsCountException extends RuntimeException {
    public ActiveRoundsCountException(String errorMessage) {
        super(errorMessage);
    }
}
