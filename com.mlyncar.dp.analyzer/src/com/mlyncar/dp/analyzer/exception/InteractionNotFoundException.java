package com.mlyncar.dp.analyzer.exception;

public class InteractionNotFoundException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = -4526097440685722393L;

	public InteractionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public InteractionNotFoundException(String message) {
        super(message);
    }
}
