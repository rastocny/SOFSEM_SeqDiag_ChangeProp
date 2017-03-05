package com.mlyncar.dp.analyzer.exception;

public class MainMethodNotFoundException extends Exception {
    public MainMethodNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public MainMethodNotFoundException(String message) {
        super(message);
    }
}
