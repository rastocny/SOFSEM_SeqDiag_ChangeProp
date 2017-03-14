package com.mlyncar.dp.synch.exception;

public class SynchronizationException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 3437400443562007862L;

    public SynchronizationException(String message, Throwable cause) {
        super(message, cause);
    }

    public SynchronizationException(String message) {
        super(message);
    }
}
