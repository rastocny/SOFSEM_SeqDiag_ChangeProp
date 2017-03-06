package com.mlyncar.dp.analyzer.exception;

public class InteractionNotFoundException extends Exception {

    public InteractionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public InteractionNotFoundException(String message) {
        super(message);
    }
}
