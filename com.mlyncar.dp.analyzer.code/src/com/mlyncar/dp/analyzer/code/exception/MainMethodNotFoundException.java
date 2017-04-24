package com.mlyncar.dp.analyzer.code.exception;

public class MainMethodNotFoundException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = -6036307848777303048L;

    public MainMethodNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public MainMethodNotFoundException(String message) {
        super(message);
    }
}
