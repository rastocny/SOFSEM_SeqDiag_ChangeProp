package com.mlyncar.dp.comparison.exception;

public class SignatureTypeException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 6132298913904693654L;

    public SignatureTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public SignatureTypeException(String message) {
        super(message);
    }
}
