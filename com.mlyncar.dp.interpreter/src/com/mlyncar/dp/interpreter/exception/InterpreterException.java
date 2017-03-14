package com.mlyncar.dp.interpreter.exception;

public class InterpreterException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6422746031174472907L;

	public InterpreterException(String message, Throwable cause) {
        super(message, cause);
    }

    public InterpreterException(String message) {
        super(message);
    }
}
