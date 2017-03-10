package com.mlyncar.dp.comparison.exception;

public class ComparisonException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9145420695185197295L;

	public ComparisonException(String message, Throwable cause) {
        super(message, cause);
    }

    public ComparisonException(String message) {
        super(message);
    }
}
